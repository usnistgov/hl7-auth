package gov.nist.hit.hl7.auth.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.format.annotation.DateTimeFormat;

import gov.nist.hit.hl7.auth.domain.AccountLog;
import java.util.Date;

@RepositoryRestResource(collectionResourceRel = "accountLog", path = "accountLog")
public interface AccountLogRepository extends MongoRepository<AccountLog, String> {
    List<AccountLog> findByUsername(String username);
    List<AccountLog> findByFrom(String from);
    List<AccountLog> findByUsernameAndFrom(String username, String from);

    List<AccountLog> findByDateBetween(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate);
    List<AccountLog> findByDateBetweenAndUsername(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, String username);
    List<AccountLog> findByDateBetweenAndFrom(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, String from);
    List<AccountLog> findByDateBetweenAndUsernameAndFrom(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, String username, String from);
}
