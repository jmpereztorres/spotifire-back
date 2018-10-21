package com.spotifire.web.rest.client;

import com.spotifire.persistence.pojo.Evidence;

public interface ITwitterClientService {

	void fetchTwitter();

	void reportEvidence(Evidence evidence);
}
