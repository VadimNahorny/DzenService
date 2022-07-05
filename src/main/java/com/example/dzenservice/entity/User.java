package com.example.dzenservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String login;
    private String password;
    @Lob
    private byte[] photo;

    @OneToOne(cascade = CascadeType.ALL)
    Follower follower = new Follower();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Follower> followers;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Follower> following;

    @JsonIgnore
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<Tag, Long> preferenceMap;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Long> idShownPosts;

    public void setLogin(String login) {
        this.login = login;
        follower.setName(login);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
        follower.setPhoto(photo);
    }

    public void setId(long id) {
        this.id = id;
        follower.setId(id);
    }

    public void setFollower(Follower follower) {
        follower.setName(this.login);
        follower.setPhoto(this.photo);
        follower.setId(this.id);
        this.follower = follower;
    }
}
