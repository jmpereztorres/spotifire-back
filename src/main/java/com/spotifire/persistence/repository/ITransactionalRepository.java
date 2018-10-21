package com.spotifire.persistence.repository;

import java.util.List;

import com.spotifire.persistence.pojo.IPojo;

public interface ITransactionalRepository {

	<T> Long countByExample(T example);

	void delete(IPojo object);

	<T> List<T> findByExample(T example);

	<T> T getObjectById(Long id, Class<T> clazz);

	<T> T save(T object);

}
