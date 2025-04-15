package org.dci;

import org.dci.client.MovieDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFixtures {
    public static final Integer USER_ID = 1;
    public static final String SIGNUP_USERNAME1 = "user1";
    public static final String PASSWORD = "Test@11234";
    public static final String QUERY = "Best movies ever";
    public static final String RESPONSE = "Title: \"\"The Shawshank Redemption\"\", ReleaseDate: 1994-09-23, Overview: Imprisoned in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates -- including an older prisoner named Red -- for his integrity and unquenchable sense of hope., Rating: 8.7\n" +
            "Title: \"\"The Dark Knight\"\", ReleaseDate: 2008-07-16, Overview: Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets. The partnership proves to be effective, but they soon find themselves prey to a reign of chaos unleashed by a rising criminal mastermind known to the terrified citizens of Gotham as the Joker., Rating: 8.519\n" +
            "Title: \"\"Pulp Fiction\"\", ReleaseDate: 1994-09-10, Overview: A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time., Rating: 8.489\n" +
            "Title: \"\"Inception\"\", ReleaseDate: 2010-07-15, Overview: Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"\"inception\"\", the implantation of another person's idea into a target's subconscious., Rating: 8.369\".";

    public static final List<MovieDetails> MOVIE_LIST = new ArrayList<>(Arrays.asList(new MovieDetails("The Shawshank Redemption", "1994-09-23", "Imprisoned in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates -- including an older prisoner named Red -- for his integrity and unquenchable sense of hope.", "8.7")));

}
