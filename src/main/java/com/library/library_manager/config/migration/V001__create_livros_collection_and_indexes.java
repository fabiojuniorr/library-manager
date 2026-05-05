package com.library.library_manager.config.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "V001_create_livros_collection_and_indexes", order = "001", author = "library-manager")
public class V001__create_livros_collection_and_indexes {

	@Execution
	public void execution(MongoTemplate mongoTemplate) {
		if (!mongoTemplate.collectionExists("livros")) {
			mongoTemplate.createCollection("livros");
		}

		mongoTemplate.indexOps("livros")
				.createIndex(new Index().on("isbn", Sort.Direction.ASC).unique());
	}

	@RollbackExecution
	public void rollback(MongoTemplate mongoTemplate) {
		if (mongoTemplate.collectionExists("livros")) {
			mongoTemplate.dropCollection("livros");
		}
	}
}
