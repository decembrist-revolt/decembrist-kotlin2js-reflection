package org.decembrist.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.decembrist.domain.content.KtFileContent;
import org.decembrist.parsers.SourceParser;

import java.io.File;
import java.util.List;

import static java.util.Collections.singletonList;

@Mojo(name = "process",
		defaultPhase = LifecyclePhase.PROCESS_SOURCES,
		requiresProject = true,
		threadSafe = true,
		requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ProcessAnnotationsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
	private File sourceDirectory;

	/**
	 * Sources folders for processing
	 *
	 * default: ${project.build.sourceDirectory}
	 */
	@Parameter
	private List<File> sorceDirs;

	public void execute() throws MojoExecutionException {
		final SourceParser sourceParser = new SourceParser(getSourceDirs());
		final List<KtFileContent> contentList = sourceParser.parse();
	}

	private List<File> getSourceDirs() {
		List<File> result = singletonList(sourceDirectory);
		if (sorceDirs != null && !sorceDirs.isEmpty()) {
			result = sorceDirs;
		}
		return result;
	}
}
