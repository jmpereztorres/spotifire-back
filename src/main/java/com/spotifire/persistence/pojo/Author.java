
package com.spotifire.persistence.pojo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.spotifire.persistence.constants.SourceType;

@Entity
@Table(name = "AUTHOR")
public class Author implements IPojo {

	private static final long serialVersionUID = -1226244226635320029L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String alias;

	private Date creationDate;

	private SourceType source;

	/**
	 * Default constructor
	 */
	public Author() {
		super();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public SourceType getSource() {
		return this.source;
	}

	public void setSource(SourceType source) {
		this.source = source;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Author other = (Author) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Author [id=").append(this.id).append(", alias=").append(this.alias).append(", creationDate=")
				.append(this.creationDate).append(", source=").append(this.source).append("]");
		return builder.toString();
	}

}
