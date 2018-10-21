
package com.spotifire.persistence.pojo;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;

@Entity
@Table(name = "REPORT")
public class Report implements IPojo {

	private static final long serialVersionUID = 4678731533593796259L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Location location;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Author author;

	@ManyToOne
	private Evidence evidence;

	private Date creationDate;

	private Boolean hasImage;

	@Transient
	private byte[] image;

	private Integer imageScore;

	private SourceType source;

	private String description;

	private ReportType type;

	private Long twitterId;

	private Integer score;

	/**
	 * Default constructor
	 */
	public Report() {
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

	public ReportType getType() {
		return type;
	}

	public void setType(ReportType fire) {
		this.type = fire;
	}

	public Author getAuthor() {
		return this.author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Evidence getEvidence() {
		return this.evidence;
	}

	public void setEvidence(Evidence evidence) {
		this.evidence = evidence;
	}

	public Boolean getHasImage() {
		return this.hasImage;
	}

	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Integer getImageScore() {
		return this.imageScore;
	}

	public void setImageScore(Integer imageScore) {
		this.imageScore = imageScore;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTwitterId() {
		return this.twitterId;
	}

	public void setTwitterId(Long twitterId) {
		this.twitterId = twitterId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
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
		Report other = (Report) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [id=").append(this.id).append(", location=").append(this.location).append(", author=").append(this.author)
				.append(", evidence=").append(this.evidence).append(", creationDate=").append(this.creationDate).append(", hasImage=")
				.append(this.hasImage).append(", image=").append(Arrays.toString(this.image)).append(", imageScore=")
				.append(this.imageScore).append(", source=").append(this.source).append(", description=").append(this.description)
				.append(", type=").append(this.type).append("]");
		return builder.toString();
	}

}
