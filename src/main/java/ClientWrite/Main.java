package ClientWrite;

import java.util.Scanner;

import AjouterLigneFichier.AjouterLigneFichier ;

import sendFinout.SendFinout;

public class Main {
    public static void main(String []args) throws Exception{

        // initializing the scanner
        Scanner scanner = new Scanner(System.in);

        // initializing the AjouterLigneFichier
        AjouterLigneFichier ajoutLigne = new AjouterLigneFichier("ClientWrite");

        //initializing the sendFinout class
        SendFinout sendFinout = new SendFinout("WRITE");

        System.out.println(" hello ! \n Every line you write here will be automatically added to the file 'fichier.txt' and replicated to online servers. \n \n ");

        int ligneNumber = 0 ;
        String ligneContent , text;

        while(true){
            // ligne number incrementation
            ligneNumber ++ ;

            // scanning the content and reforming it
            ligneContent =" "+ligneNumber+"     ";
            System.out.print(ligneContent);
            ligneContent  += scanner.nextLine();

            // writing it in the file fichier.txt in the repository ClientWriter
            ajoutLigne.ajouterLigne(ligneContent);

            // sending it to all the channels connected to the exchange WRITE
            sendFinout.send(ligneContent);

        }

    }

}