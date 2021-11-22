package ru.gsa.biointerface.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "sample")
@Table(name = "sample")
public class Sample implements Serializable, Comparable<Sample> {
    @NotNull(message = "Id can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    @EmbeddedId
    private SampleID id;

    @NotNull(message = "Channel can't be null")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id", nullable = false),
            @JoinColumn(name = "channel_number", referencedColumnName = "number", nullable = false)
    })
    @MapsId("channel_id")
    private Channel channel;

    @NotNull(message = "Value can't be null")
    @Column(nullable = false)
    private int value;

    public Sample(int id, Channel channel, int value) {
        this.id = new SampleID(id, channel.getId());
        this.channel = channel;
        channel.getSamples().add(this);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample that = (Sample) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Sample o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
