package gov.nist.hit.hl7.auth.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import gov.nist.hit.hl7.auth.domain.AccountLog;

@RepositoryRestResource(collectionResourceRel = "accountLog", path = "accountLog")
public interface AccountLogRepository extends MongoRepository<AccountLog, String> {
}
