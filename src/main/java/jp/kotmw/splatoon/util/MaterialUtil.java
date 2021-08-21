package jp.kotmw.splatoon.util;

import org.bukkit.Material;

public class MaterialUtil {

    public static boolean isColorable(Material m){
        return isWool(m)|| isCarpet(m)||isStainedClay(m)||isStainedGlass(m)||isStainedGlassPane(m);
    }

    public static boolean isPaintable(Material m){
        return isWool(m)|| isCarpet(m);
    }

    public static boolean isSign(Material m){
        return isWallSign(m) || isStandingSign(m);
    }

    public static boolean isWool(Material m){

            if (m == Material.WHITE_WOOL ) return true;
            else if (m == Material.BLACK_WOOL ) return true;
            else if (m == Material.BLUE_WOOL ) return true;
            else if (m == Material.CYAN_WOOL ) return true;
            else if (m == Material.ORANGE_WOOL ) return true;
            else if (m == Material.LIGHT_BLUE_WOOL ) return true;
            else if (m == Material.LIGHT_GRAY_WOOL ) return true;
            else if (m == Material.MAGENTA_WOOL ) return true;
            else if (m == Material.RED_WOOL ) return true;
            else if (m == Material.PINK_WOOL ) return true;
            else if (m == Material.BROWN_WOOL ) return true;
            else if (m == Material.GREEN_WOOL ) return true;
            else if (m == Material.YELLOW_WOOL ) return true;
            else if (m == Material.GRAY_WOOL ) return true;
            else if (m == Material.LIME_WOOL ) return true;
            else if (m == Material.PURPLE_WOOL ) return true;
            else if (m == Material.LEGACY_WOOL ) return true;
        return false;
    }

    public static boolean isCarpet(Material m){
      if (m == Material.WHITE_CARPET ) return true;
            else if (m == Material.BLACK_CARPET ) return true;
            else if (m == Material.BLUE_CARPET ) return true;
            else if (m == Material.CYAN_CARPET ) return true;
            else if (m == Material.ORANGE_CARPET ) return true;
            else if (m == Material.LIGHT_BLUE_CARPET ) return true;
            else if (m == Material.LIGHT_GRAY_CARPET ) return true;
            else if (m == Material.MAGENTA_CARPET ) return true;
            else if (m == Material.RED_CARPET ) return true;
            else if (m == Material.PINK_CARPET ) return true;
            else if (m == Material.BROWN_CARPET ) return true;
            else if (m == Material.GREEN_CARPET ) return true;
            else if (m == Material.YELLOW_CARPET ) return true;
            else if (m == Material.GRAY_CARPET ) return true;
            else if (m == Material.LIME_CARPET ) return true;
            else if (m == Material.PURPLE_CARPET ) return true;
      else if (m == Material.LEGACY_CARPET ) return true;

        return false;
    }

    public static boolean isStainedGlassPane(Material m){
       if (m == Material.WHITE_STAINED_GLASS_PANE) return true;
            else if (m == Material.BLACK_STAINED_GLASS_PANE) return true;
            else if (m == Material.BLUE_STAINED_GLASS_PANE) return true;
            else if (m == Material.CYAN_STAINED_GLASS_PANE) return true;
            else if (m == Material.ORANGE_STAINED_GLASS_PANE) return true;
            else if (m == Material.LIGHT_BLUE_STAINED_GLASS_PANE) return true;
            else if (m == Material.LIGHT_GRAY_STAINED_GLASS_PANE) return true;
            else if (m == Material.MAGENTA_STAINED_GLASS_PANE) return true;
            else if (m == Material.RED_STAINED_GLASS_PANE) return true;
            else if (m == Material.PINK_STAINED_GLASS_PANE) return true;
            else if (m == Material.BROWN_STAINED_GLASS_PANE) return true;
            else if (m == Material.GREEN_STAINED_GLASS_PANE) return true;
            else if (m == Material.YELLOW_STAINED_GLASS_PANE) return true;
            else if (m == Material.GRAY_STAINED_GLASS_PANE) return true;
            else if (m == Material.LIME_STAINED_GLASS_PANE) return true;
            else if (m == Material.PURPLE_STAINED_GLASS_PANE) return true;
       else if (m == Material.LEGACY_STAINED_GLASS_PANE) return true;

        return false;
    }

    public static boolean isStainedGlass(Material m){
       if (m == Material.WHITE_STAINED_GLASS) return true;
            else if (m == Material.BLACK_STAINED_GLASS) return true;
            else if (m == Material.BLUE_STAINED_GLASS) return true;
            else if (m == Material.CYAN_STAINED_GLASS) return true;
            else if (m == Material.ORANGE_STAINED_GLASS) return true;
            else if (m == Material.LIGHT_BLUE_STAINED_GLASS) return true;
            else if (m == Material.LIGHT_GRAY_STAINED_GLASS) return true;
            else if (m == Material.MAGENTA_STAINED_GLASS) return true;
            else if (m == Material.RED_STAINED_GLASS) return true;
            else if (m == Material.PINK_STAINED_GLASS) return true;
            else if (m == Material.BROWN_STAINED_GLASS) return true;
            else if (m == Material.GREEN_STAINED_GLASS) return true;
            else if (m == Material.YELLOW_STAINED_GLASS) return true;
            else if (m == Material.GRAY_STAINED_GLASS) return true;
            else if (m == Material.LIME_STAINED_GLASS) return true;
            else if (m == Material.PURPLE_STAINED_GLASS) return true;
       else if (m == Material.LEGACY_STAINED_GLASS) return true;

        return false;
    }

    public static boolean isStainedClay(Material m){
        if (m == Material.WHITE_TERRACOTTA) return true;
            else if (m == Material.BLACK_TERRACOTTA) return true;
            else if (m == Material.BLUE_TERRACOTTA) return true;
            else if (m == Material.CYAN_TERRACOTTA) return true;
            else if (m == Material.ORANGE_TERRACOTTA) return true;
            else if (m == Material.LIGHT_BLUE_TERRACOTTA) return true;
            else if (m == Material.LIGHT_GRAY_TERRACOTTA) return true;
            else if (m == Material.MAGENTA_TERRACOTTA) return true;
            else if (m == Material.RED_TERRACOTTA) return true;
            else if (m == Material.PINK_TERRACOTTA) return true;
            else if (m == Material.BROWN_TERRACOTTA) return true;
            else if (m == Material.GREEN_TERRACOTTA) return true;
            else if (m == Material.YELLOW_TERRACOTTA) return true;
            else if (m == Material.GRAY_TERRACOTTA) return true;
            else if (m == Material.LIME_TERRACOTTA) return true;
            else if (m == Material.PURPLE_TERRACOTTA) return true;
        else if (m == Material.LEGACY_STAINED_CLAY) return true;

        return false;
    }

    public static boolean isStandingSign(Material m){
        if (m == Material.SPRUCE_SIGN) return true;
            else if (m == Material.ACACIA_SIGN) return true;
            else if (m == Material.BIRCH_SIGN) return true;
            else if (m == Material.CRIMSON_SIGN) return true;
            else if (m == Material.DARK_OAK_SIGN) return true;
            else if (m == Material.JUNGLE_SIGN) return true;
            else if (m == Material.OAK_SIGN) return true;
            else if (m == Material.WARPED_SIGN) return true;
        else if (m == Material.LEGACY_SIGN) return true;
        else if (m == Material.LEGACY_SIGN_POST) return true;

        return false;
    }

    public static boolean isWallSign(Material m){
        if (m == Material.SPRUCE_WALL_SIGN) return true;
            else if (m == Material.ACACIA_WALL_SIGN) return true;
            else if (m == Material.BIRCH_WALL_SIGN) return true;
            else if (m == Material.CRIMSON_WALL_SIGN) return true;
            else if (m == Material.DARK_OAK_WALL_SIGN) return true;
            else if (m == Material.JUNGLE_WALL_SIGN) return true;
            else if (m == Material.OAK_WALL_SIGN) return true;
            else if (m == Material.WARPED_WALL_SIGN) return true;
        else if (m == Material.LEGACY_WALL_SIGN) return true;

        return false;
    }

    public static Material fromColorIdToStainedGlass(int id){
        switch(id){
            case 0:
                return Material.WHITE_STAINED_GLASS;
            case 1:
                return Material.ORANGE_STAINED_GLASS;
            case 2:
                return Material.MAGENTA_STAINED_GLASS;
            case 3:
                return Material.LIGHT_BLUE_STAINED_GLASS;
            case 4:
                return Material.YELLOW_STAINED_GLASS;
            case 5:
                return Material.LIME_STAINED_GLASS;
            case 6:
                return Material.PINK_STAINED_GLASS;
            case 7:
                return Material.GRAY_STAINED_GLASS;
            case 8:
                return Material.LIGHT_GRAY_STAINED_GLASS;
            case 9:
                return Material.CYAN_STAINED_GLASS;
            case 10:
                return Material.PURPLE_STAINED_GLASS;
            case 11:
                return Material.BLUE_STAINED_GLASS;
            case 12:
                return Material.BROWN_STAINED_GLASS;
            case 13:
                return Material.GREEN_STAINED_GLASS;
            case 14:
                return Material.RED_STAINED_GLASS;
            case 15:
                return Material.BLACK_STAINED_GLASS;
            default:
                return Material.WHITE_STAINED_GLASS;
        }
    }

    public static Material fromColorIdToCarpet(int id) {
        switch (id) {
            case 0:
                return Material.WHITE_CARPET;
            case 1:
                return Material.ORANGE_CARPET;
            case 2:
                return Material.MAGENTA_CARPET;
            case 3:
                return Material.LIGHT_BLUE_CARPET;
            case 4:
                return Material.YELLOW_CARPET;
            case 5:
                return Material.LIME_CARPET;
            case 6:
                return Material.PINK_CARPET;
            case 7:
                return Material.GRAY_CARPET;
            case 8:
                return Material.LIGHT_GRAY_CARPET;
            case 9:
                return Material.CYAN_CARPET;
            case 10:
                return Material.PURPLE_CARPET;
            case 11:
                return Material.BLUE_CARPET;
            case 12:
                return Material.BROWN_CARPET;
            case 13:
                return Material.GREEN_CARPET;
            case 14:
                return Material.RED_CARPET;
            case 15:
                return Material.BLACK_CARPET;
            default:
                return Material.WHITE_CARPET;
        }
    }

    public static Material fromColorIdToWool(int id) {
        switch (id) {
            case 0:
                return Material.WHITE_WOOL;
            case 1:
                return Material.ORANGE_WOOL;
            case 2:
                return Material.MAGENTA_WOOL;
            case 3:
                return Material.LIGHT_BLUE_WOOL;
            case 4:
                return Material.YELLOW_WOOL;
            case 5:
                return Material.LIME_WOOL;
            case 6:
                return Material.PINK_WOOL;
            case 7:
                return Material.GRAY_WOOL;
            case 8:
                return Material.LIGHT_GRAY_WOOL;
            case 9:
                return Material.CYAN_WOOL;
            case 10:
                return Material.PURPLE_WOOL;
            case 11:
                return Material.BLUE_WOOL;
            case 12:
                return Material.BROWN_WOOL;
            case 13:
                return Material.GREEN_WOOL;
            case 14:
                return Material.RED_WOOL;
            case 15:
                return Material.BLACK_WOOL;
            default:
                return Material.WHITE_WOOL;
        }
    }

    public static Material fromColorIdToBanner(int id) {
        switch (id) {
            case 0:
                return Material.WHITE_BANNER;
            case 1:
                return Material.ORANGE_BANNER;
            case 2:
                return Material.MAGENTA_BANNER;
            case 3:
                return Material.LIGHT_BLUE_BANNER;
            case 4:
                return Material.YELLOW_BANNER;
            case 5:
                return Material.LIME_BANNER;
            case 6:
                return Material.PINK_BANNER;
            case 7:
                return Material.GRAY_BANNER;
            case 8:
                return Material.LIGHT_GRAY_BANNER;
            case 9:
                return Material.CYAN_BANNER;
            case 10:
                return Material.PURPLE_BANNER;
            case 11:
                return Material.BLUE_BANNER;
            case 12:
                return Material.BROWN_BANNER;
            case 13:
                return Material.GREEN_BANNER;
            case 14:
                return Material.RED_BANNER;
            case 15:
                return Material.BLACK_BANNER;
            default:
                return Material.WHITE_BANNER;
        }
    }
}
