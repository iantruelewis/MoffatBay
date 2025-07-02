package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import model.DataManager;

public class ReservationLookup implements Serializable {

    private String searchInput;
    private List<Reservation> reservationResults = new ArrayList<>();
    private boolean searched = false;

    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }

    public List<Reservation> getReservationResults() {
        return reservationResults;
    }

    public void setReservationResults(List<Reservation> reservationResults) {
        this.reservationResults = reservationResults;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public String search() {
        DataManager dm = new DataManager();
        searched = true;

        if (searchInput != null && searchInput.contains("@")) {
            reservationResults = dm.findReservationsByEmail(searchInput.trim());
        } else {
            try {
                int resId = Integer.parseInt(searchInput.trim());
                reservationResults = dm.findReservationById(resId);
            } catch (NumberFormatException e) {
                reservationResults = new ArrayList<>();
            }
        }

        return null;
    }
}