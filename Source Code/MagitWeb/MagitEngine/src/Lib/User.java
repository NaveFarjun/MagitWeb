package Lib;

import java.util.Objects;

public class User {
    private String m_Name;

    public User(String name) {
        this.m_Name = name;
    }

    public String getName() {
        return m_Name;
    }

    @Override
    public String toString() {
        return m_Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(m_Name, user.m_Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_Name);
    }
}
