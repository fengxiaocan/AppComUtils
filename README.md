# AppComUtils
Android app项目使用到的工具类,以后会慢慢添加
添加权限申请管理工具
使用方法:在Activity/Fragment实现ActivityCompat.OnRequestPermissionsResultCallback和EasyPermissions.PermissionCallbacks接口
实现接口的方法:

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsSuccess(int requestCode, List<String> perms) {
        /*申请权限成功*/
        try {
            // TODO 有权限,执行方法
        } catch (Exception e) {
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show();
            MyAppUtils.openSettingActivity(this);
        }
    }

    @Override
    public void onPermissionsDafeat(int requestCode, List<String> perms) {
        Toast.makeText(this, "请到设置界面打开权限设置", Toast.LENGTH_SHORT).show();
         /*申请权限失败,跳到设置界面*/
        AppUtils.openSettingActivity(this);
    }


创建申请权限的方法:
    @AfterPermissionGranted(CAMERA_AND_READ_AND_WIRTE)
    private void requestPermissionToCamera() {
        String[] perms = {Manifest.permission.CAMERA,
                          Manifest.permission.READ_EXTERNAL_STORAGE,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //已经有权限了
            mImageUtils.choseImageFromCamera();
        } else {
            EasyPermissions.requestPermissions(this, "申请使用相机权限、使用内存权限用于拍照以及保存照片、读取照片", CAMERA_AND_READ_AND_WIRTE, perms);
            //进入到这里代表没有权限.
        }
    }
然后调用该方法就可以了.
EmailUtils:邮件发送工具,但是要确保邮箱的IMAP功能开启,一般只能使用授权码登录
ApkUtils:apk操作工具,可以实现静默安装与卸载(需要root)
AppUtils:app工具,聚集了所有的与app相关的方法

混淆须知

-keep class com.sun.mail.** {*;}
-keep class javax.mail.** {*;}
-keep class com.fxc.lib.utils.** {*;}
-keep class com.sun.activation.registries.** {*;}
-keep class javax.activation.** {*;}