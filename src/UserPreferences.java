import java.util.prefs.Preferences;

public class UserPreferences {
    private Preferences userPrefs;
    public UserPreferences()
    {
        userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
    }
}
