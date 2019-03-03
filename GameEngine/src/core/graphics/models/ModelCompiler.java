package core.graphics.models;

import java.io.BufferedReader;
import java.io.IOException;

import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.utils.fileSystem.FileManager;

public class ModelCompiler {
	
	/**
	 * Create a model with texture etc from a single ini file.
	 * @param f - The file containing info about the model to load.
	 * @return a model
	 * @throws IOException
	 * @throws ShaderCompileException 
	 */
	public static ModelBlueprint loadModelBlueprint(String f) throws IOException, ShaderCompileException {
		if (!f.contains(".ini")) {
			throw new RuntimeException("Unsupported file excension.");
		}
		BufferedReader reader = FileManager.getReader(f);
		String line = "";
		String[] splittedLine;
		ModelBlueprint model;
		String modelFile = "", textureFile = "", shader = "";
		while  ((line = reader.readLine()) != null) {
			
			line = line.replaceAll("\\s","");
			if(line.startsWith("//")) {
				continue;
			}
			splittedLine = line.split("=");
			if (splittedLine.length != 2) {
				throw new IOException(splittedLine[0] + " property is empty in " + f);
			}
			
			/*
			 * See if the line contains a file path. Put that file path in a string which then can be
			 * used to create the model with associated shaders and textures.
			 */
			if (splittedLine[0].contains(Settings.MODEL.getName())) {
				modelFile = splittedLine[1];
			} else if (splittedLine[0].contains(Settings.TEXTURE.getName())) {
				textureFile = splittedLine[1];
			} else if (splittedLine[0].contains(Settings.SHADER.getName())) {
				shader = splittedLine[1];
			}
		}
		reader.close();
		
		// Create a private shader program for the model.
		Shaders shaderProgram = new Shaders(shader);
		
		reader.close();
		
		// Create the model and return it.
		model = new ModelBlueprint(modelFile, textureFile);
		model.setShader(shaderProgram);
		return model;
	}
	
	/**
	 * This represents the available settings in a model ini file.
	 * @author Robin
	 *
	 */
	public static enum Settings {
		MODEL("model"), TEXTURE("texture"), SHADER("shader");
		
		String name = "";
		
		private Settings(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	
	public static void compileModel(ModelBlueprint m) {
		throw new RuntimeException("Method not suppoerted");
	}
}
