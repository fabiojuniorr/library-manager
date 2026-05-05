package com.library.library_manager;

import org.springframework.boot.SpringApplication;

public class TestLibraryManagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(LibraryManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
