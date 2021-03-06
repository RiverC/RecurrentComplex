/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ivorius.ivtoolkit.tools.IvFileHelper;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.loading.FileLoaderRegistry;
import ivorius.reccomplex.files.loading.RCFileSuffix;
import ivorius.reccomplex.files.saving.FileSaverAdapter;
import ivorius.reccomplex.json.NbtToJson;
import ivorius.reccomplex.world.gen.feature.structure.StructureInfo;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.utils.ByteArrays;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by lukas on 25.05.14.
 */
public class StructureSaveHandler
{
    public static final StructureSaveHandler INSTANCE = new StructureSaveHandler(RCFileSuffix.STRUCTURE, StructureRegistry.INSTANCE);

    public static final String STRUCTURE_INFO_JSON_FILENAME = "structure.json";
    public static final String WORLD_DATA_NBT_FILENAME = "worldData.nbt";

    public final Gson gson;

    public String suffix;
    public StructureRegistry registry;

    public StructureSaveHandler(String suffix, StructureRegistry registry)
    {
        gson = createGson();
        this.suffix = suffix;
        this.registry = registry;
    }

    protected static void addZipEntry(ZipOutputStream zip, String path, byte[] bytes) throws IOException
    {
        ZipEntry jsonEntry = new ZipEntry(path);
        zip.putNextEntry(jsonEntry);
        jsonEntry.setSize(bytes.length);
        zip.write(bytes);
        zip.closeEntry();
    }

    public Gson createGson()
    {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(GenericStructureInfo.class, new GenericStructureInfo.Serializer());
        StructureRegistry.TRANSFORMERS.constructGson(builder);
        StructureRegistry.GENERATION_INFOS.constructGson(builder);

        NbtToJson.registerSafeNBTSerializer(builder);

        return builder.create();
    }

    public GenericStructureInfo fromJSON(String jsonData) throws JsonSyntaxException
    {
        return gson.fromJson(jsonData, GenericStructureInfo.class);
    }

    public String toJSON(GenericStructureInfo structureInfo)
    {
        return gson.toJson(structureInfo, GenericStructureInfo.class);
    }

    public GenericStructureInfo structureInfoFromResource(ResourceLocation resourceLocation)
    {
        try
        {
            return structureInfoFromZip(new ZipInputStream(IvFileHelper.inputStreamFromResourceLocation(resourceLocation)));
        }
        catch (Exception ex)
        {
            RecurrentComplex.logger.error("Could not read generic structure " + resourceLocation.toString(), ex);
        }

        return null;
    }

    public GenericStructureInfo structureInfoFromZip(ZipInputStream zipInputStream) throws StructureLoadException
    {
        try
        {
            String json = null;
            NBTTagCompound worldData = null;

            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null)
            {
                byte[] bytes = ByteArrays.completeByteArray(zipInputStream);

                if (bytes != null)
                {
                    if (STRUCTURE_INFO_JSON_FILENAME.equals(zipEntry.getName()))
                        json = new String(bytes);
                    else if (WORLD_DATA_NBT_FILENAME.equals(zipEntry.getName()))
                        worldData = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes));
                }

                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            if (json == null || worldData == null)
                throw new StructureInvalidZipException(json != null, worldData != null);

            GenericStructureInfo genericStructureInfo = fromJSON(json);
            genericStructureInfo.worldDataCompound = worldData;

            return genericStructureInfo;
        }
        catch (IOException e)
        {
            throw new StructureLoadException(e);
        }
    }

    public class Loader extends FileLoaderRegistry<GenericStructureInfo>
    {
        public Loader()
        {
            super(StructureSaveHandler.this.suffix, StructureSaveHandler.this.registry);
        }

        @Override
        public GenericStructureInfo read(Path path, String name) throws Exception
        {
            try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(path)))
            {
                return structureInfoFromZip(zip);
            }
        }
    }

    public class Saver extends FileSaverAdapter<StructureInfo>
    {
        public Saver(String id)
        {
            super(id, StructureSaveHandler.this.suffix, StructureSaveHandler.this.registry);
        }

        @Override
        public void saveFile(Path path, StructureInfo structureInfo) throws Exception
        {
            GenericStructureInfo structure = structureInfo.copyAsGenericStructureInfo();

            if (structure == null)
                throw new IllegalArgumentException();

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(path)))
            {
                addZipEntry(zipOutputStream, STRUCTURE_INFO_JSON_FILENAME, toJSON(structure).getBytes());
                addZipEntry(zipOutputStream, WORLD_DATA_NBT_FILENAME, ByteArrays.toByteArray(s -> CompressedStreamTools.writeCompressed(structure.worldDataCompound, s)));

                zipOutputStream.close();
            }
        }
    }
}
