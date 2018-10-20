package com.spotifire.persistence.repository;

import java.io.Serializable;
import java.util.List;

public interface ITransactionalRepository {

	<T> Long countByExample(T example);

	void delete(Serializable object);

	<T> List<T> findByExample(T example);

	<T> T getObjectById(Serializable id, Class<T> clazz);

	<T> T save(T object);

}
