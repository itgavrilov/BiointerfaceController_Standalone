package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channelName")
@Table()
public class ChannelName implements Serializable, Comparable<ChannelName> {
    @NotNull(message = "Id can't be null")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    @Column(length = 35, unique = true, nullable = false)
    private String name;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Channels can't be null")
    @OneToMany(mappedBy = "channelName", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    public ChannelName() {
    }

    public ChannelName(String name, String comment) {
        this.id = -1;
        this.name = name;
        this.comment = comment;
        this.channels = new ArrayList<>();
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

    public void addChannel(Channel channel) {
        if (channel == null)
            throw new NullPointerException("Channel is null");

        channel.setChannelName(this);

        if (!channels.contains(channel)) {
            channels.add(channel);
        }
    }

    public void deleteChannel(Channel channel) {
        if (channel == null)
            throw new NullPointerException("Channel is null");

        channels.remove(channel);

        if (channel.getChannelName().equals(this)) {
            channel.setChannelName(null);
        }
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
        int result = 0;

        if (id > o.id) {
            result = 1;
        } else if (id < o.id) {
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
