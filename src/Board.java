public class Board {

    private static final String[] PICTURES = {
            """
    -----   
    |       
    |       
    |       
    |       
    ------- 
    """,
            """
     -----   
     |   |   
     |   O   
     |       
     |       
     ------- 
     """,
            """
     -----   
     |   |   
     |   O   
     |   |   
     |       
     ------- 
     """,
            """
     -----   
     |   |   
     |   O   
     |  /|   
     |       
     ------- 
     """,
            """
     -----   
     |   |   
     |   O   
     |  /|\\ 
     |       
     ------- 
     """,
            """
     -----   
     |   |   
     |   O   
     |  /|\\ 
     |  /    
     ------- 
     """,
            """
     -----   
     |   |   
     |   O   
     |  /|\\ 
     |  / \\ 
     ------- 
     """,
    };

    public static String getPicture(int errorsCount) {
        return PICTURES[errorsCount];
    }
}
