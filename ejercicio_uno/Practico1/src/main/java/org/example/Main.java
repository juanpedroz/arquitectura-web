package org.example;

import org.example.utils.HelperMySQL;

public class Main {
    static void main() {

        HelperMySQL mySQLdb = new HelperMySQL();

        try {
            mySQLdb.setUpDatabase();
            mySQLdb.closeConnection();

        }catch (Exception e) {
            System.out.println(e);
        }

    }
}
