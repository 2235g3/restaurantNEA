package rs.cs.restaurantnea.general.objects;

public class Search {
    private String Text;
    private String Filter;
    private String SortBy;

    public Search(String text, String filter, String sortBy) {
        Text = text;
        Filter = filter;
        SortBy = sortBy;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public String getSortBy() {
        return SortBy;
    }

    public void setSortBy(String sortBy) {
        SortBy = sortBy;
    }
}
