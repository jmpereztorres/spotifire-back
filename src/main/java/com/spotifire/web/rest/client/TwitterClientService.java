package com.spotifire.web.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.persistence.repository.ITransactionalRepository;

@Service
public class TwitterClientService implements ITwitterClientService {

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Override
	public void fetchTwitter() {
	}

}
