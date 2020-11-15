/* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 *   这个文件来自 GOSCPS(https://github.com/GOSCPS)
 *   使用 GOSCPS 许可证
 *   File:    Main.java
 *   Content: Bg Main Source File
 *   Copyright (c) 2020 GOSCPS 保留所有权利.
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * */

package team.goscps.bg.main;

import team.goscps.bg.build.RepoBuild;
import team.goscps.bg.config.RepoConfig;
import team.goscps.bg.tool.Tools;

import java.io.File;

public class Main {

    public static final String Bg_Version = "00.01.00";

    //版本号
    //vBg_ApiVersion.Bg_FunctionVersion.Bg_FixVersion
    public static final int Bg_ApiVersion = 0;
    public static final int Bg_FunctionVersion = 1;
    public static final int Bg_FixVersion = 0;

    //主类
    public static void main(String[] args) {
        System.out.println("====================");
        System.out.println("   Bg From GOSCPS");
        System.out.printf("  Version %-8s  \n", Bg_Version);
        System.out.println("====================");

        //无参数
        if (args.length == 0) {
            Tools.Red_Println("Command Not Input!");
            Tools.Red_Println("Input Command `help` To Get Help");
        }

        //help
        //打印帮助列表
        else if (args[0].equals("help")) {
            Tools t = new Tools();
            try {
                System.out.println(t.Read_From_Jar("/Help.txt"));
            } catch (Exception err) {
                err.printStackTrace();
                Tools.Red_Println("Error:Help.txt read error");
                Tools.Red_Println("Check Your Install");
            }
        }

        //创建新仓库
        else if (args[0].equals("new")) {
            if (args.length >= 2) {
                CreateNewRepo(args[1]);
            } else {
                CreateNewRepo("bg");
            }
        }

        //构造
        else if (args[0].equals("build")) {
            //读取配置文件
            RepoConfig c = new RepoConfig();
            try {
                c =
                        Tools.JsonToObject(
                                Tools.Read_All_Text(
                                        Tools.Make_File_Path(
                                                System.getProperty("user.dir")
                                                , "Config.json"))
                                , c.getClass());

                if (c == null) {
                    throw new Exception("Read Config Is Null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Tools.Red_Println("Error:Read Config Error");
                return;
            }

            RepoBuild b = null;
            //构建
            try {
                b = new RepoBuild(c);
            }
            catch(Exception err){
                err.printStackTrace();
                Tools.Red_Println("Build Error");
                return;
            }

            //解析参数
            if (args.length >= 2) {
                String[] param = new String[args.length - 1];
                System.arraycopy(args, 1, param, 0, args.length - 1);

                //失败则退出
                if (!b.Setting(param)) {
                    return;
                }

            }
            b.Start();
        } else {
            Tools.Red_Println("Error:Command Not Found");
        }
    }

    /**
     * 新建一个仓库
     *
     * @param name 仓库名称
     */
    public static void CreateNewRepo(String name) {
        System.out.printf("Create Repo %s ...\n", name);

        //初始化工作->检查
        {
            String Base_Path = System.getProperty("user.dir");
            File f;
            f = new File(Tools.Make_File_Path(Base_Path, name));

            if (f.exists()) {
                Tools.Red_Println("Error:Dir is exists");
                System.out.printf("At %s\n", f.getPath());
                return;
            }

            if (!f.mkdir()) {
                Tools.Red_Println("Error:Dir Create Error");
                System.out.printf("At %s\n", f.getPath());
                return;
            }
        }

        String Base_Path = Tools.Make_File_Path(System.getProperty("user.dir"), name);

        //创建各种目录
        if (!Tools.Mkdirs(Tools.Make_File_Path(Base_Path, "Source"))) {
            Tools.Red_Println("Error:Dir Create Error");
            System.out.printf("At %s\n", Tools.Make_File_Path(Base_Path, "Source"));
            return;
        }

        if (!Tools.Mkdirs(Tools.Make_File_Path(Base_Path, "Site"))) {
            Tools.Red_Println("Error:Dir Create Error");
            System.out.printf("At %s\n", Tools.Make_File_Path(Base_Path, "Site"));
            return;
        }

        if (!Tools.Mkdirs(Tools.Make_File_Path(Base_Path, "Plugin"))) {
            Tools.Red_Println("Error:Dir Create Error");
            System.out.printf("At %s\n", Tools.Make_File_Path(Base_Path, "Plugin"));
            return;
        }

        if (!Tools.Mkdirs(Tools.Make_File_Path(Base_Path, "Config"))) {
            Tools.Red_Println("Error:Dir Create Error");
            System.out.printf("At %s\n", Tools.Make_File_Path(Base_Path, "Config"));
            return;
        }

        if (!Tools.Mkdirs(Tools.Make_File_Path(Base_Path, "Pages"))) {
            Tools.Red_Println("Error:Dir Create Error");
            System.out.printf("At %s\n", Tools.Make_File_Path(Base_Path, "Pages"));
            return;
        }

        RepoConfig c = new RepoConfig();
        c.name = name;
        c.defined.put("GFM","true");

        try {
            Tools.Write_All_Text(Tools.Make_File_Path(Base_Path, "Config.json"), Tools.ObjectTOJson(c));
        } catch (Exception e) {
            e.printStackTrace();
            Tools.Red_Println("Error:Create File Error");
        }

        Tools.Green_Println("Create Successful!");
    }

}
