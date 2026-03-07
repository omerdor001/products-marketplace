package service;

import domain.Admins_Memory_Repository;

public class AdminService {
    private static AdminService instance;
    private Admins_Memory_Repository adminsRepository;

    private AdminService() {
        adminsRepository = new Admins_Memory_Repository();
    }

    public static AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }

    //For testing purposes only
    static void resetInstance() {
        instance = null;
    }

    public boolean login(String username, String password) {
        return adminsRepository.login(username, password);
    } 
    
    public void addAdmin(String username, String password) {
        adminsRepository.addAdmin(username, password);
    }
}
