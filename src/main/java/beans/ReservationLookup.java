package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataManager;

public class ReservationLookup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchInput;
    private List<Reservation> reservationResults = new ArrayList<>();
    private boolean searched = false;


    private Map<Integer, Boolean> expandedMap = new HashMap<>();

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

    public boolean isExpanded(int resId) {
        return expandedMap.getOrDefault(resId, false);
    }

    public void toggleExpand(int resId) {
        boolean current = expandedMap.getOrDefault(resId, false);
        expandedMap.put(resId, !current);
    }

    public Map<Integer, Boolean> getExpandedMap() {
        return expandedMap;
    }
    
    public String toggleLabel(int resId) {
        return isExpanded(resId) ? "Hide Details" : "Show Details";
    }

    public void setExpandedMap(Map<Integer, Boolean> expandedMap) {
        this.expandedMap = expandedMap;
    }
}