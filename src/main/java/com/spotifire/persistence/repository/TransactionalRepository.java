package com.spotifire.persistence.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.SpotifireConstants;

@Repository
@Transactional
public class TransactionalRepository implements ITransactionalRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public <T> Long countByExample(T example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Serializable object) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> findByExample(T example) {
		return null;
	}

	@Override
	public <T> T getObjectById(Serializable id, Class<T> clazz) {
		return em.find(clazz, id);
	}

	@Override
	public <T> T save(T object) {
		// em.getTransaction().begin();
		if (object != null && SpotifireUtils.callReflectionMethod(object, SpotifireConstants.METHOD_GET_ID, null, null) != null) {
			em.persist(object);
		} else {
			em.merge(object);
		}
		// em.getTransaction().commit();
		return object;
	}

}
