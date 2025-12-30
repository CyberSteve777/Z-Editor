Z-Editor 是一款专为《植物大战僵尸2:中文版》关卡制作设计的 Android 可视化编辑器。
本项目旨在解决传统 JSON 编辑中容易出现的语法错误、引用丢失以及繁琐的波次计算问题。通过现代化的移动端 UI，降低 JSON 关卡编辑门槛。

✨ 核心功能 (Features)

📂 1. 完善的文件管理
直接读写: 基于 Android SAF (Storage Access Framework)，直接对手机存储中的 JSON 文件进行读写，无需反复导入导出。
文件操作: 支持关卡的新建（基于模板）、复制（自动处理重命名冲突）、重命名及彻底删除。
权限管理: 首次启动引导授权，支持记住工作目录。

🛠 2. 强大的可视化编辑
所见即所得: 告别枯燥的代码，通过直观的卡片和表单配置关卡参数。
模块化系统: 自动识别关卡包含的模块（如种子库、传送带、阳光掉落），并动态生成对应的编辑 Tab。
智能校验:
自动检测模块冲突（如同时存在传送带和种子库）。
实时检测失效的事件引用并提供一键清理功能。

🌊 3. 波次时间轴
可视化编排: 清晰展示每一波包含的事件。
交互操作: 支持右滑管理事件、左滑删除波次。
点数计算: 内置点数计算逻辑，实时预览每一波的僵尸点数及旗帜波状态。
期望分析: 点击点数可查看当前波次的僵尸出怪期望概率。

🧩 4. 深度定制编辑器
已实现的专用编辑器包括：
传送带: 可视化编辑植物池权重、等级限制。支持分段配置刷新延迟和传输速度。
自然出怪: 支持按行配置僵尸列表。支持精英僵尸标记与等级配置。
其它事件: 支持传送门、风暴突袭、海盗登船等事件的参数配置。

📚 5. 内置数据库
集成植物与僵尸的静态数据资源。
提供带图标的搜索选择器，支持按品质、属性、作用筛选植物。

🏗 技术栈 (Tech Stack)

语言: Kotlin
UI 框架: Jetpack Compose (Material3)
架构: MVVM (Repository Pattern)
数据解析: Gson
动画: Compose Animation (AnimatedContent, SwipeToDismiss)
文件系统: Android DocumentFile API

📂 项目结构 (Project Structure)

code
Text
com.example.pvz2leveleditor
├── data/                  // 数据层
│   ├── PvzDataModels.kt   // Pvz2 JSON 数据结构定义
│   ├── Repository/        // 组件仓库
│   │   ├── LevelRepository.kt     // 文件读写与管理
│   │   ├── PlantRepository.kt     // 植物静态数据资源
│   │   ├── ReferenceRepository.kt // 引用对象缓存
│   └── ...
├── views/                 // UI 层
│   ├── screens/           // 主要屏幕
│   ├── editor/            // 编辑器组件
│   │   ├── pages/         // 具体模块的编辑页面
│   │   └── ...
│   └── theme/             // Compose 主题配置
└── MainActivity.kt        // 应用入口与导航


🚀 快速开始 (Getting Started)

克隆本项目到本地。
使用 Android Studio (Ladybug 或更高版本) 打开项目。
确保你的 Assets 目录中包含必要的资源文件：
assets/resources/Plants.json (植物数据库)
assets/template/ (关卡模板文件)
assets/reference/ (参考模块定义)
assets/images/ (对应的植物/僵尸图标)
连接 Android 设备或模拟器运行。
在应用中选择一个包含 Pvz2 关卡 JSON 的文件夹即可开始编辑。

📅 开发计划 (Roadmap)

基础文件管理与 JSON 解析

波次时间轴与点数计算

传送带与自然出怪编辑器

砸罐子可视化布局编辑器

僵王属性配置页面

撤销/重做功能

更多事件类型的专用编辑器


⚠️ 免责声明
本项目仅供学习交流使用。请勿用于商业用途。使用本工具修改游戏文件可能会导致游戏崩溃，请在编辑前务必备份原始文件。
