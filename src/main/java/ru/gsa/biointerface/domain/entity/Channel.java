package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channel")
@Table(name = "channel")
public class Channel implements Serializable, Comparable<Channel> {
    @Id
    @GeneratedValue(generator = "sqlite_channel")
    @TableGenerator(name = "sqlite_channel", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "channel",
            initialValue = 1, allocationSize = 1)
    private long id;

    @Column(length = 35, unique = true)
    private String name;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Graph> graphs;

    public Channel() {
    }

    public Channel(long id, String name, String comment, List<Graph> graphs) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.graphs = graphs;
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

    public List<Graph> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel that = (Channel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Channel o) {
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
        return "Channel{" +
                "name='" + name + '\'' +
                '}';
    }
}
