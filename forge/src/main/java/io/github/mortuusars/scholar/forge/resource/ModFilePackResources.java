package io.github.mortuusars.scholar.forge.resource;

import java.nio.file.Path;

import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;

public class ModFilePackResources extends PathPackResources {
	protected final IModFile modFile;
	protected final String sourcePath;

	public ModFilePackResources(String name, IModFile modFile, String sourcePath, boolean isBuiltIn) {
		super(name, isBuiltIn, modFile.findResource(sourcePath));
		this.modFile = modFile;
		this.sourcePath = sourcePath;
	}

	@Override
	@NotNull
	protected Path resolve(String... paths) {
		String[] allPaths = new String[paths.length + 1];
		allPaths[0] = sourcePath;
		System.arraycopy(paths, 0, allPaths, 1, paths.length);
		return modFile.findResource(allPaths);
	}
}