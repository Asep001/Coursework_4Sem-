package sample.DataBase;

public interface Registrationable {
    long registrateUser(String name, String password);
    long logInUser(String name, String password);
}
