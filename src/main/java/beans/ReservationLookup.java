package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataManager;
import model.ResHelper;

public class ReservationLookup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchInput;
    private List<ResHelper> reservationResults = new ArrayList<>();
    private boolean searched = false;


    private Map<Integer, Boolean> expandedMap = new HashMap<>();

    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }

    public List<ResHelper> getReservationResults() {
		return reservationResults;
	}

	public void setReservationResults(List<ResHelper> reservationResults) {
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
            setReservationResults(dm.findReservationsByEmail(searchInput.trim()));
        } else {
            try {
                int resId = Integer.parseInt(searchInput.trim());
                setReservationResults(dm.findReservationById(resId));
            } catch (NumberFormatException e) {
                setReservationResults(new ArrayList<>());
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