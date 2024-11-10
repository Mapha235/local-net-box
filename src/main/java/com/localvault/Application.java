package com.localvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// @Bean
	// CommandLineRunner init(FileRepository fileRepository) {		
	// 	return args-> {
	// 		String rootDir = Constants.HOST_ROOT;
	// 		File directoryPath = new File(rootDir);
	// 		String contents[] = directoryPath.list();
	// 		Stream.of(contents).forEach(file -> {
	// 			FileEntity fileEnt = new FileEntity(file, "");
	// 			fileRepository.save(fileEnt);
	// 		});
	// 		// fileRepository.findAll().forEach(System.out::println);
	// 	};
	// }
}
