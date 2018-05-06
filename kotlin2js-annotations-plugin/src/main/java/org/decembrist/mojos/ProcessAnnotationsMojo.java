package org.decembrist.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.decembrist.domain.content.KtFileContent;
import org.decembrist.generators.ReflectionUtilsGenerator;
import org.decembrist.parsers.SourceParser;
import org.decembrist.services.logging.Logger;
import org.decembrist.services.logging.LoggerService;
import org.jetbrains.annotations.NotNull;

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
	 * Fully qualified name of class with main function
	 */
	@Parameter(required = true)
	private String mainClass;

	/**
	 * Sources folders for processing
	 * <p>
	 * default: ${project.build.sourceDirectory}
	 */
	@Parameter
	private List<File> sorceDirs;

	public void execute() throws MojoExecutionException {
		LoggerService.INSTANCE.setLogger(new EnvLogger(getLog()));

		final SourceParser sourceParser = new SourceParser(getSourceDirs());
		final List<KtFileContent> contentList = sourceParser.parse();
		final ReflectionUtilsGenerator generator = new ReflectionUtilsGenerator(mainClass);
		generator.generateCode(contentList);
	}

	private List<File> getSourceDirs() {
		List<File> result = singletonList(sourceDirectory);
		if (sorceDirs != null && !sorceDirs.isEmpty()) {
			result = sorceDirs;
		}
		return result;
	}

	static class EnvLogger implements Logger {

		private Log logger;

		public EnvLogger(Log logger) {
			this.logger = logger;
		}

		public void debug(@NotNull String message) {
			logger.debug(message);
		}

		public void warn(@NotNull String message) {
			logger.warn(message);
		}
	}
}
