# MILite 项目整理与配方审计

日期：2026-10-14。以本仓库代码和生成配方为依据，使用本机 DeepSeek Harness 做了只读复核，再逐项核对源码。

本轮目标是清理 MI 分支遗留和修正确定错误。火药加速、超速模块按作者决定保留；机器等级的固定速度倍率继续使用。

## 已修正

| 问题 | 原行为 | 修正 |
|---|---|---|
| 超导线增殖 | `materials/assembler/superconductor_cable` 消耗 3 根超导线、2 个冷却单元、1000 mB PVC，产出 6 根相同线材 | 删除失去电缆产物后已无用途的配方定义及生成物，保留正常线材生产、回收和线圈配方 |
| FE 接口方向 | 输入接口仍允许调用 `extractEnergy`，输出接口仍允许调用 `receiveEnergy` | 方法内部检查方向，模拟调用同样受限 |
| FE 存档与容量边界 | 负数存档可产生负能量；能力接口直接报告未按当前容量截断的内部值 | 非负截断并统一能力接口与内部读取；保留旧 `storedEu` 存档键 |
| 钢级蒸汽多方块速度 | 等级倍率与额外倍率都计算钢级加速，基础变成 4 倍 | 钢级基础 2 倍，火药激活后 4 倍 |
| KubeJS 能源字段 | schema 写 `eu`，机器配方加载器读取 `energy` | 以 `energy` 为输出字段，保留 `eu` 输入别名和 `.eu(...)`，同时支持 `.energy(...)`、`.fe(...)` |
| KubeJS 配方等级 | schema 缺少 `level`，无法正常设置最低机器等级 | 加入 `.level(String)`，使用与 datagen 一致的名称校验、大小写处理和 `aluminum` 别名；省略仍为青铜 |
| 多方块等级检查 | 部分多方块没有登记最高等级，检查时直接跳过 | 补齐内置及 KubeJS 简单多方块登记；未登记且有配方的类型输出警告 |
| 测试过滤 | 使用已失效的 `forge.enabledGameTestNamespaces`，额外运行第三方测试 | 改为 NeoForge 当前的 `neoforge.enabledGameTestNamespaces`，执行 MILite 全部用例 |
| 元数据生成依赖 | 重设资源目录时丢失生成任务依赖，构建可能使用旧的模组描述与依赖声明 | 资源目录引用生成任务，构建自动更新元数据 |
| 模组依赖声明 | 没有声明 Minecraft、NeoForge、GuideME 要求和 KubeJS 加载顺序 | 补齐必需依赖及可选 KubeJS 的先后顺序 |
| 资源打包 | 生成器 `.cache` 被装进 jar；重复资源默认静默覆盖 | 排除缓存，资源重复时构建失败；移除重复的 Java 源目录配置 |
| 手册与开发文档 | 仍介绍渐进超频、润滑剂加速、旧电压等级、已失效接口参数 | 更新中英文核心手册、README 和配方／机器／等级开发文档 |

主要代码：

- [EnergyComponent](../src/main/java/aztech/milite/machines/components/EnergyComponent.java)
- [SteamCraftingMultiblockBlockEntity](../src/main/java/aztech/milite/machines/blockentities/multiblocks/SteamCraftingMultiblockBlockEntity.java)
- [MachineRecipeSchema](../src/main/java/aztech/milite/compat/kubejs/recipe/MachineRecipeSchema.java)
- [MachineKubeRecipe](../src/main/java/aztech/milite/compat/kubejs/recipe/MachineKubeRecipe.java)
- [MachineLevelValidator](../src/main/java/aztech/milite/machines/init/MachineLevelValidator.java)
- [模组元数据](../src/main/templates/META-INF/neoforge.mods.toml)

## 死代码清理

删除了无库内消费者的旧管道桥接接口 `CableTierHolder`、`WhitelistedItemStorage`，及其实现类的空转方法；删除 `EnergyStorage.EMPTY`、`CasingComponent.canInsertFe` 和空的升级注册事件。

删除未被构造、注册或引用的 `IOFluidHandler`、`HotIngotColoramp`、`DepletedColoramp`，以及无消费者的颜色导出、方向索引、旧物品 NBT、方向掩码、旧耐久条等辅助方法。删除前核对了源码、资源、Mixin 和注册入口。

以下名称虽来自 MI，仍有实际用途：

- `CableTier` 用于机壳、能源缓存、储能与自定义机器。其类名和 `registerCableTiers` 事件名保留兼容，文档明确实际用途。
- `FilledItemStorage` 被机器自动输入／输出使用。
- `*_machine_casing_pipe` 是多方块结构方块，不承担运输功能。
- `invar_rotary_blade` 仍用于工具制造；它与已移除的通用叶片不是同一用途。
- `OverclockComponent` 和 `OverdriveComponent` 仍承担火药加速与超速模块功能。

旧接口移除后，外部代码若直接依赖这些已废弃类型，需要调整引用。机器和物品注册 ID、现有有效配方 ID、旧能源存档键保持兼容；本轮仅删除一条失效电缆配方。

## 配方检查结果

生成配方共 **2937** 条，其中机器配方 **1865** 条。没有发现完全相同的静态 JSON 配方，也没有发现内置单方块配方所需槽位超过注册数量。

| 最低等级 | 机器配方数 |
|---|---:|
| bronze | 1469 |
| steel | 0 |
| electric | 360 |
| aluminum_alloy | 7 |
| stainless_steel | 7 |
| titanium | 0 |
| iridium | 22 |

钢级和钛级没有专属配方要求，说明它们目前主要承担速度／设备进度作用，而非每一级都解锁一组配方。打包机、拉丝机等没有青铜版本，因此配方写青铜也不意味着青铜机器能加工。

机器配方总消耗为 `energy × duration`。实际每刻需求会乘上机器等级、机器自身额外速度和可选加速；供能不足时保留进度，按实际获得能量推进。不能把旧的“配方能耗等于加工门槛”规则直接套到当前实现。

检查了纯单物料、确定产出、最多四步的转换环。未发现该范围内的金属增殖；圆石和各类岩石的增殖配方使用水／熔岩催化，是已有设计。此检查不覆盖任意长度、多输入和概率配方的全部经济循环。

配方等级回归检查覆盖所有内置机器配方类型及其当前最高等级。等级上界检查通过，说明不存在“要求等级高于任何同类机器”的内置配方；它不等同于已经证明从零开始的全部材料进度可达。

## 后续平衡重点

仍有 **129** 条青铜要求但参考能耗不为 2 的机器配方，其中以下 **12** 条可以进入蒸汽单方块机器。它们继承原 JSON 的缺省等级；datagen 迁移没有制造这些问题。

| 配方路径（`milite:` 后） | 参考能耗／时长 | 判断与建议 |
|---|---|---|
| `electric_age/component/cutting_machine/silicon_waffer` | 16／600 | 硅原料已有后续加工门槛，可考虑将切片明确限定 electric；先核对电路解锁链 |
| `materials/compressor/blastproof_alloy_plate` | 32／600 | 抗爆合金已有材料门槛；可考虑 electric，避免蒸汽机承担后期设备加工 |
| `materials/compressor/tungsten_tiny_dust_to_ingot` | 32／2400 | 用压缩机从小撮钨粉生产钨粒，可能是有意简化冶炼；先决定是否保留此替代路径 |
| `materials/mixer/itnt` | 16／200 | 工业 TNT 可在蒸汽搅拌机加工；是否要求电动设备属于玩法取舍 |
| `materials/mixer/oil_to_rubber` | 4／300 | 合成橡胶路径；若保留蒸汽时代石油化工，可维持现状 |
| `oil/mixer/diesel` | 8／600 | 燃料加工是否允许蒸汽设备，应与化工进度统一决定 |
| `oil/mixer/diethyl_ether_biodiesel_boost` | 12／450 | 同上，另有二乙醚材料门槛 |
| `oil/mixer/diethyl_ether_diesel_boost` | 12／300 | 同上 |
| `vanilla_recipes/compressor/diamond_from_coal` | 4／6000 | 长时间煤压钻石；作为早期替代路径可以保留，不必仅因能耗为 4 提高等级 |
| `vanilla_recipes/mixer/endstone` | 8／200 | 合成末地石是否应绕过维度探索，需要按整包玩法决定 |
| `vanilla_recipes/mixer/lava` | 32／1250 | 高成本造熔岩影响早期能源循环，建议实测锅炉与燃料链净收益后决定 |
| `vanilla_recipes/mixer/netherite` | 16／200 | 是否允许蒸汽时代处理下界合金，应与材料成本一起调整 |

本轮没有批量提高这些等级或改动能耗，避免让简化版原有替代路径突然失效。建议先确定“蒸汽时代保留哪些高级化工与原版资源合成”，再逐条调整 `.level(...)`，并验证所需机器、线圈、外壳和电路能够先于它解锁。

另有三项后续整理：

1. 普通电动多方块默认采用不锈钢加工等级，即使结构材料为铝合金或钛。当前是实际行为；若要改成按结构材料定等级，需要逐机决定并调整速度。
2. 储电单元与能源物品交互最多循环 10000 次，用于绕过第三方物品单次充放电上限。可优化，但直接降低次数会改变交互效果。
3. 其他语言手册和未涉及本轮的页面仍可能保留 MI 旧术语。核心中英文入门、能源和机器页面已同步；其他页面需要按语言继续核对。

DeepSeek 提出的部分建议未采用：多方块能源来自能源仓而不是 `CasingComponent`，不能据单方块缓存推导 EBF 永远达不到标称速度；64 与 16 个相同铀产物合计是正常的 80 个，拆分还会影响输出槽容纳，不能随意合并来“修复复制”。

## 验证记录

- `runData` 成功，只移除失效超导电缆配方，生成总数从 2938 变为 2937。
- 完整构建、格式与许可证检查通过。
- 修正测试命名空间后，MILite 全部 **27 项**游戏测试通过，包括新增的 FE 方向／存档边界、钢级蒸汽多方块速度、配方等级可加工性回归。
- KubeJS 实测通过：5 条验证配方真正加载，覆盖 `.level()`、大小写和铝合金别名、错误等级拒绝、三种能源方法、青铜缺省等级，以及旧 `eu` JSON 转为 `energy`。同时 27 项游戏测试全部通过。
- 不加载 KubeJS 时的启动、datagen 和全部游戏测试也通过；最终生成未再改变配方定义。
- 最终 jar 的 2937 条配方与生成文件逐条一致；失效配方和删除类均不存在，生成器缓存为 0；源码包不再包含重复源目录路径。
- 清理了 22 个空源目录／生成目录；Git 不记录空目录，因此这部分没有文件差异。

本轮没有改动版本号或增加正式依赖库。必需依赖声明对应现有运行要求，KubeJS 仍是可选项。
