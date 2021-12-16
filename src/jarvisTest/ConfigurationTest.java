package jarvisTest;

import jarvis.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {
    @Test
    public void newConfiguration() {
        Configuration config = new Configuration("src/jarvisTest/test.configuration.properties");
        Assert.assertNull(config.getString("Setting"));
        Assert.assertNull(config.getString("BadSetting"));
        Assert.assertEquals(0, config.getInt("BadSetting"));
        Assert.assertEquals(0, config.getInt("BadSetting"));
        Assert.assertEquals(0, config.getInt("IntSetting"));
        Assert.assertEquals("some/file.html", config.getString("HelloPage"));
        Assert.assertEquals("", config.getString("EmptySetting"));
        Assert.assertEquals(0, config.getInt("EmptySetting"));
        Assert.assertEquals(0, config.getInt("HelloPage"));
        Assert.assertEquals(0, config.getInt("ZeroSetting"));
        Assert.assertEquals(100, config.getInt("OneHunnit"));
        Assert.assertEquals(" Another Space ", config.getString("ExtraSpaceSetting"));
        Assert.assertEquals(" Far out man ...      ", config.getString("Setting With Spaces"));

        config.set("OneHunnit", "100");
        Assert.assertEquals(100, config.getInt("OneHunnit"));
        config.set("OneHunnit", "50");
        Assert.assertEquals(50, config.getInt("OneHunnit"));
        config.set("OneHunnit", "one hundred");
        Assert.assertEquals(0, config.getInt("OneHunnit"));
        Assert.assertEquals("one hundred", config.getString("OneHunnit"));
        config.set("HelloPage", "newFile.html");
        Assert.assertEquals("newFile.html", config.getString("HelloPage"));

        Assert.assertNull(config.getString("CommentedSetting"));
        Assert.assertNull(config.getString("#CommentedSetting"));
        Assert.assertNull(config.getString("# CommentedSetting"));
        Assert.assertNull(config.getString(" CommentedSetting"));
        Assert.assertNull(config.getString("AlsoCommented"));
        Assert.assertNull(config.getString("#AlsoCommented"));
        Assert.assertNull(config.getString("# AlsoCommented"));
        Assert.assertNull(config.getString(" AlsoCommented"));
        Assert.assertNull(config.getString("  # AlsoCommented"));
        Assert.assertNull(config.getString(" AlsoCommented"));
        Assert.assertNull(config.getString(""));
    }
}
