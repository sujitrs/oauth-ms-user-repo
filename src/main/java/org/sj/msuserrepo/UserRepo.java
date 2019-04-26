package org.sj.msuserrepo;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
public class UserRepo {
	
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@GeneratedValue(generator = "uuid2")
	private UUID id;
	
	private String name;
	private String poiFileID;
	private String poaFileID;
	
	@Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

}
