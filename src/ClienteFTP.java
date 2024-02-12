import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClienteFTP {
    public static void main(String[] args) throws IOException {
        final FTPClient ftp = new FTPClient();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FileOutputStream fos = null;

        String DEFAULT_SERVER = "ftp.nic.funet.fi";
        String DEFAULT_USER = "anonymous";

        String server;
        String user;
        String password;
        String currentDirectory;

        // Preguntar elementos necesarios: server, user, password
        System.out.print("Server: ");
        server = br.readLine();

        // si no se introduce el servidor se usa el servidor por defecto
        if(server.isEmpty()) {
            server = DEFAULT_SERVER;
            System.out.println("Using default server: " + DEFAULT_SERVER);
        }

        System.out.print("User: ");
        user = br.readLine();

        // si no se introduce el usuario se usa el usuario por defecto
        if(user.isEmpty()) {
            user = DEFAULT_USER;
            System.out.println("Using default user: " + DEFAULT_USER);
        }

        // si el usuario es anónimo usamos una contraseña predeterminada
        // (hecho por conveniencia, no recomendable)
        if(user.equalsIgnoreCase("anonymous")) {
            user = "anonymous";
            password = "anonymous@anonymous";
        } else {
            System.out.print("Password: ");
            password = br.readLine();
        }


        boolean error = false;
        try {
            int reply;
            ftp.connect(server);
            System.out.println("Connected to " + server + ".");
            System.out.print(ftp.getReplyString());

            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            if(!ftp.login(user, password)) {
                System.err.println("Could not login.");
                System.exit(1);
            }

            System.out.println("Logged in as " + user);

            currentDirectory = ftp.printWorkingDirectory();

            System.out.print(user + ":" + currentDirectory + "> ");

            String input;
            while(!(input = br.readLine()).equalsIgnoreCase("QUIT")) {
                if(input.equalsIgnoreCase("PWD")) {
                    System.out.println("Current directory is " + ftp.printWorkingDirectory());
                } else if(input.equalsIgnoreCase("CDUP")) {
                    ftp.changeToParentDirectory();
                    currentDirectory = ftp.printWorkingDirectory();
                    System.out.println("Current directory is " + currentDirectory);
                } else if(input.equalsIgnoreCase("LIST")) {
                    for(FTPFile file : ftp.listFiles()) {
                        if(file != null) {
                            System.out.println(file);
                        }
                    }
                } else if(input.equalsIgnoreCase("PASV")) {
                    ftp.enterLocalPassiveMode();
                    System.out.println("Changed to passive mode");
                } else if(input.equalsIgnoreCase("PORT")) {

                } else if(input.equalsIgnoreCase("HELP")) {
                    System.out.println("HELP");
                } else if(input.toUpperCase().startsWith("CWD")) {
                    // dividimos el input del usuario en argumentos
                    String[] arguments = input.split(" ");

                    // comprobamos que metan el num de argumentos que queremos
                    if(arguments.length != 2) {
                        System.out.println("Wrong number of arguments");
                        continue;
                    }

                    ftp.changeWorkingDirectory(arguments[1]);
                    currentDirectory = ftp.printWorkingDirectory();
                    System.out.println("Current directory is " + currentDirectory);
                } else if(input.equalsIgnoreCase("DELE")) {

                } else if(input.equalsIgnoreCase("MKD")) {

                } else if(input.equalsIgnoreCase("RMD")) {

                } else if(input.equalsIgnoreCase("MODE")) {

                } else if(input.equalsIgnoreCase("NOOP")) {

                } else if(input.equalsIgnoreCase("USER")) {

                } else if(input.equalsIgnoreCase("PASS")) {

                } else if(input.toUpperCase().startsWith("RETR")) {
                    /*
                    *
                    * Los archivos se guardan de manera predeterminada en la carpeta del proyecto
                    *
                    * */

                    String local;
                    String remote;

                    // dividimos el input del usuario en argumentos
                    String[] arguments = input.split(" ");

                    // comprobamos que metan el num de argumentos que queremos
                    if(arguments.length < 2 || arguments.length > 3) {
                        System.out.println("Wrong number of arguments");
                        continue;
                    }

                    // si hay dos argumentos el nombre del archivo será igual en local y en remoto
                    local = arguments[1];
                    remote = arguments[1];

                    // si hay un tercer argumento será asignado al local
                    if(arguments.length == 3) {
                        local = arguments[2];
                    }

                    // lanzo IOException
                    fos = new FileOutputStream(local);
                    ftp.retrieveFile(remote, fos);
                    System.out.println(remote + " downloaded");
                } else if(input.equalsIgnoreCase("SITE")) {

                } else if(input.equalsIgnoreCase("STAT")) {

                } else if(input.equalsIgnoreCase("STOR")) {

                } else if(input.equalsIgnoreCase("STOU")) {

                } else if(input.equalsIgnoreCase("SYST")) {

                } else if(input.equalsIgnoreCase("TYPE")) {

                } else {
                    System.out.println("Command not recognized");
                }
                System.out.println(ftp.getReplyString());
                System.out.print(user + ":" + currentDirectory + "> ");
            }
            ftp.logout();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
            System.exit(error ? 1 : 0);
        }
    }
}