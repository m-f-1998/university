/**
 * @create date 10-02-2021 21:42:32
 * @modify date 19-02-2021 09:26:22
 * @desc [System Constants]
 */

using System.Collections.Generic;

public class Constants {
    public const int LIVES = 3;
    public const int HEIGHT = 300;
    public const int WIDTH = 300;

    public static readonly Dictionary<int, Dictionary<string, int>> keys =
        new Dictionary<int, Dictionary<string, int>>() {
            { 1,  new Dictionary<string, int>() {
                {"NUM_NORMAL_KEYS", 2},
                {"NUM_DEADLY_KEYS", 1},
                {"NUM_SECRET_KEYS", 1}
            }},
            { 2,  new Dictionary<string, int>() {
                {"NUM_NORMAL_KEYS", 0},
                {"NUM_DEADLY_KEYS", 0},
                {"NUM_SECRET_KEYS", 0}
            }},
            { 3,  new Dictionary<string, int>() { // Adding more normal keys would require more colours
                {"NUM_NORMAL_KEYS", 2},
                {"NUM_DEADLY_KEYS", 1},
                {"NUM_SECRET_KEYS", 1}
            }}
        };

    public static readonly string[] spawnable = {"SafeGround"};
    public static readonly Dictionary<int, string[]> colorsStandard =  new Dictionary<int, string[]>(){
        {0, new string[] {"Purple", "#FF0000", "#0000FF"}}, // Purple, Red, Blue
        {1, new string[] {"Grey", "#000000", "#FFFFFF"}}, // Grey, Black, White
        {2, new string[] {"Pink", "#FF0000", "#FFFFFF"}}, // Pink, Red, White
        {3, new string[] {"Cyan", "#00FF00", "#0000FF"}}, // Cyan, Green, Blue
    };

    public static readonly string[] colorsDeadly = {
        "#53152c", "#c27545", "#8b5b83"
    };
}