package edu.uob.supporters;

import java.util.ArrayList;

import edu.uob.DBServer;
import edu.uob.commands.AlterCMD;
import edu.uob.commands.CreateCMD;
import edu.uob.commands.DeleteCMD;
import edu.uob.commands.DropCMD;
import edu.uob.commands.InsertCMD;
import edu.uob.commands.JoinCMD;
import edu.uob.commands.SelectCMD;
import edu.uob.commands.UpdateCMD;
import edu.uob.commands.UseCMD;

// Parser creates approriate child class of DBcmd parent reference using first token
public class Parser {

    public Parser(){
    }

    public DBcmd parse(ArrayList<String> tokens) {
        DBcmd output;
        output = switch (tokens.get(0).toLowerCase()) {
            case "use" -> new UseCMD();
            case "create" -> new CreateCMD();
            case "drop" -> new DropCMD();
            case "alter" -> new AlterCMD();
            case "insert" -> new InsertCMD();
            case "select" -> new SelectCMD();
            case "update" -> new UpdateCMD();
            case "delete" -> new DeleteCMD();
            case "join" -> new JoinCMD();
            default -> new DBcmd() {
                @Override
                public String query(ArrayList<String> tokens, DBServer dbServer) {
                    return "[ERROR] \nInvalid command type.";
                }
            };
        };
        return output;
    }
}
