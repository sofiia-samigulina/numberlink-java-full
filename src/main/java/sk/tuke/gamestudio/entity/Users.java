package sk.tuke.gamestudio.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@NamedQuery( name = "Users.findAll",
        query = "SELECT s FROM Users s")
@NamedQuery( name = "Users.getPassword",
        query = "SELECT s FROM Users s where s.player=:player")
public class Users implements Serializable {

    @Id
    @GeneratedValue
    private int ident;

    private String player;
    private String password;

    public Users() {

    }

    public Users(String player, String password) {
        this.player = player;
        this.password = password;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    @Override
    public String toString() {
        return "User{" +
                "player='" + player + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
