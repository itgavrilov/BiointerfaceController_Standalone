package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channelName")
@Table(name = "channelName")
public class ChannelName implements Serializable, Comparable<ChannelName> {
    @Id
    @GeneratedValue()
//    @GeneratedValue(generator = "sqlite_channel")
//    @TableGenerator(name = "sqlite_channel", table = "sqlite_sequence",
//            pkColumnName = "name", valueColumnName = "seq",
//            pkColumnValue = "channelName",
//            initialValue = 1, allocationSize = 1)
    private long id;

    @Column(length = 35, unique = true)
    private String name;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "channelName", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    public ChannelName() {
    }

    public ChannelName(long id, String name, String comment, List<Channel> channels) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.channels = channels;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelName that = (ChannelName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ChannelName o) {
        int result =  0;

        if(id > o.id) {
            result = 1;
        } else if(id < o.id){
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        return "ChannelName{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "comment='" + comment + '\'' +
                '}';
    }
}
