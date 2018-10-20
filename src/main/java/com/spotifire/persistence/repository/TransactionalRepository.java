package com.spotifire.persistence.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.IPojo;

@Repository
@Transactional
public class TransactionalRepository implements ITransactionalRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public <T> Long countByExample(T example) {
		Long size = 0L;
		List<T> results = this.findByExample(example);

		if (results != null) {
			size = (long) results.size();
		}
		return size;
	}

	@Override
	public void delete(IPojo object) {

		if (object != null) {
			Object persistedPojo = em.find(object.getClass(), object.getId());
			em.remove(persistedPojo);
		}

		em.close();

	}

	@Override
	public <T> List<T> findByExample(T example) {

		List<T> results = null;
		if (example != null && example instanceof IPojo) {
			/////////////////////////////////

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = (CriteriaQuery<T>) cb.createQuery(example.getClass());
			Root<T> r = (Root<T>) cq.from(example.getClass());
			Predicate p = cb.conjunction();
			Metamodel mm = em.getMetamodel();

			EntityType<T> et = (EntityType<T>) mm.entity(example.getClass());
			Set<Attribute<? super T, ?>> attrs = et.getAttributes();
			for (Attribute<? super T, ?> a : attrs) {
				String name = a.getName();
				String javaName = a.getJavaMember().getName();
				Object attributeValue = SpotifireUtils.callReflectionMethod(example,
						SpotifireConstants.METHOD_GET + StringUtils.capitalize(javaName), null, null);

				if (IPojo.class.isAssignableFrom(a.getJavaType()) && attributeValue != null) {

					EntityType<?> et2 = mm.entity(attributeValue.getClass());
					Set<?> attrs2 = et2.getAttributes();

					// TODO: recursivo
					for (Object a2 : attrs2) {
						Attribute attr2Aux = ((Attribute<? super T, ?>) a2);
						String name2 = attr2Aux.getName();
						String javaName2 = attr2Aux.getJavaMember().getName();

						Object attributeValue2 = SpotifireUtils.callReflectionMethod(attributeValue,
								SpotifireConstants.METHOD_GET + StringUtils.capitalize(javaName2), null, null);
						if (attributeValue2 != null) {
							Join<Object, Object> r2 = r.join(name);
							p = cb.and(p, cb.equal(r2.get(name2), attributeValue2));
						}
					}
					//

				} else {
					if (attributeValue != null) {
						p = cb.and(p, cb.equal(r.get(name), attributeValue));
					}
				}
			}
			cq.select(r).where(p);
			TypedQuery<T> query = em.createQuery(cq);
			///////////////////////////////////////
			System.out.println(query.unwrap(org.hibernate.query.Query.class).getQueryString());
			results = query.getResultList();
		}

		em.close();
		if (results != null) {
			results = results.stream().distinct().collect(Collectors.toList());
		}
		return results;

	}

	@Override
	public <T> T getObjectById(IPojo id, Class<T> clazz) {
		T object = em.find(clazz, id);
		em.close();
		return object;
	}

	@Override
	public <T> T save(T object) {

		if (object != null && object instanceof IPojo && ((IPojo) object).getId() != null) {
			em.merge(object);
		} else {
			em.persist(object);
		}

		em.close();
		return object;
	}

}
