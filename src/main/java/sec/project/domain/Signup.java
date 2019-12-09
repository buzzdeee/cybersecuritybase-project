package sec.project.domain;

import java.util.Objects;
import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Signup extends AbstractPersistable<Long> implements Comparable<Signup> {

    private String name;
    private String address;

    public Signup() {
    }

    public Signup(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.address);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Signup other = (Signup) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        
        return Objects.equals(this.address, other.address);
    }

    @Override
    public int compareTo(Signup o) {
        return this.name.compareTo(o.name);
    }
    
}
