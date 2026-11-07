# 上游与 Lite 对照

核对日期：2026-10-14。比较对象为上游 `1.21.x` 和当前 Lite 工作区（包含尚未提交的整理改动）。以下表格记录首次分析结果，随后按用户决定同步四项，实施情况见下节。未进行游戏性能基准对比。

## 本次同步结果

- 同步普通第三方 IItemHandler 的满箱检查，保留自身 FilledItemStorage 的快速判定。按接口报告的槽位容量判定，避免把可超堆叠的库存提前视为装满。
- 注册 `modern_tech:built_multiblock` FTB 任务，支持具体机器及可选结构形状；有效结构匹配后为放置者所在队伍推进任务，禁止手动点击完成。关闭集成时，物品、流体、建造任务均停止推进。
- `compat.ftbQuestsIntegration` 从 `modern_tech-startup.toml` 移到 `modern_tech-server.toml`。新安装默认开启；首次升级时读取旧开关，作为缺失服务端设置的初始值；已有服务端设置优先。服务端文件沿用 NeoForge 的存档 serverconfig 管理，开发游戏测试使用 config 目录。
- NeoForge 升至 `21.1.238`，FTB Quests 升至 `2101.1.36`，FTB Teams 升至 `2101.1.9`（与 Quests 所需版本对齐），FTB Library 由传递依赖解析为 `2101.1.36`。FTB Quests 仍可不安装；安装时最低版本为 `2101.1.36`。
- 补充任务中英文翻译和游戏测试；GuideME 的 NEITHER 布局差异按本次范围暂未调整。
- 验证：完整 build、runData 成功；无 FTB 和安装上述 FTB 组件的环境均通过全部 30 项游戏测试。新增检查覆盖满箱/空槽/自定义容量/超堆叠库存、自身库存快速判定、旧开关迁移、任务 NBT 与网络往返、禁止手动完成、具体形状及任意形状匹配，以及关闭集成、队伍锁定和正确建造时的任务进度。

## 比较基线

- 上游最新提交：[602d520656](https://github.com/AztechMC/Modern-Industrialization/commit/602d5206560e0ae07fced8d874b198f92c6c6f29)，北京时间 2026-10-13。
- Git 共同祖先：[550389695f](https://github.com/AztechMC/Modern-Industrialization/commit/550389695fab55c4328c3645188ed4ba37341878)，2026-10-05，使用 NeoForge SizedIngredient。
- 上游在共同祖先后新增 13 个提交。Lite 独立实现了其中多项，不能把提交数量当作缺失功能数量。
- 对照同版本的 1.21.x；另有 26.1 移植分支，但它是另一 Minecraft 版本的开发分支。

## 13 项更新逐项核对

| 上游提交 | 更新 | Lite 当前情况 |
| --- | --- | --- |
| [#1403](https://github.com/AztechMC/Modern-Industrialization/commit/602d520656) | 第三方库存完全装满时，提前跳过插入尝试 | **未同步**。FilledItemStorage 仅识别自身接口，普通 IItemHandler 不检查满箱。 |
| [#1401](https://github.com/AztechMC/Modern-Industrialization/commit/eaa0b164b7) | GuideME 的 NEITHER 类别不显示 EU 信息 | **需要按 Lite 设计适配**。Lite 已将文字改为机器等级；NEITHER 图标不绘制，但仍占据 9×9 布局空间。等级文字应单独判断是否有意义，不能连同 EU 文本一起直接删除。 |
| [#1400](https://github.com/AztechMC/Modern-Industrialization/commit/15e7e122a1) | Jade 合并流体显示 | **已具备**。按 FluidVariant 聚合数量和容量，返回统一 ViewGroup。 |
| [#1398](https://github.com/AztechMC/Modern-Industrialization/commit/66c014d52c) | 修复限定结构形状的多方块进度 | **已有等价实现**。BuiltMultiblockTrigger 比较具体形状 ID，空形状条件允许任意形状。 |
| [#1389](https://github.com/AztechMC/Modern-Industrialization/commit/2d2b25dc45) | Crowdin 更新 | 该提交实际删除 13 种语言的 181 条旧翻译行，主要是过期进度描述。属于**语言资源清理**，不是新增玩法；应按 Lite 自己的进度和 FE 文案核对，不整体覆盖。 |
| [#1397](https://github.com/AztechMC/Modern-Industrialization/commit/53f6ab3efb) | 机器产出流体可完成对应桶物品的 FTB 任务 | **已具备**。流体产出接入统计，并转换成对应桶物品计数。 |
| [#1396](https://github.com/AztechMC/Modern-Industrialization/commit/13893cadf5) | FTB 任务缓存优化、配置迁移 | **部分已具备**。已有物品任务缓存及任务文件缓存清理事件；尚未采用上游默认开启的 SERVER 配置，Lite 仍为默认关闭的 STARTUP 配置。 |
| [#1391](https://github.com/AztechMC/Modern-Industrialization/commit/a4c678edb9) | 新增 FTB 多方块建造任务 | **未同步**。原版进度触发器已具备，但没有注册 FTB 的 built_multiblock 任务类型。 |
| [#1395](https://github.com/AztechMC/Modern-Industrialization/commit/c3927abfdd) | PVC 为液体，氯乙烯为气体 | **已具备**。MIFluids 的状态设置一致。 |
| [#1394](https://github.com/AztechMC/Modern-Industrialization/commit/76d50fd48d) | 锅炉水转蒸汽倍率可配置 | **已具备**。steamPerWater 默认 16、范围 1–1000，SteamHeaterComponent 实际读取此配置。 |
| [#1393](https://github.com/AztechMC/Modern-Industrialization/commit/85cef4ece3) | 4 个线压缩为 1 个白色羊毛 | **已具备**。VanillaCompatRecipesProvider 包含相同输入输出、能耗和时长。 |
| [#1392](https://github.com/AztechMC/Modern-Industrialization/commit/fea479b02d) | 修正英文进度文案 | **保留内容已修正**。聚变、量子剑对应文案已修正；超导电缆进度在 Lite 中已移除。 |
| [#1386](https://github.com/AztechMC/Modern-Industrialization/commit/509fc6348a) | 多方块进度要求实际建成 | **已具备**。有效结构匹配时触发 BuiltMultiblockTrigger，不只是制作控制器。 |

## 建议移植顺序

1. **满箱插入优化。** Lite 依赖第三方物流，仍保留机器自动输出，因此这项有实际价值。应测试普通箱子及常用第三方库存；上游也提示某些 getStackInSlot 会复制物品栈，逐槽扫描不保证对所有实现都更快。此项是减少无效尝试，不是修复物品丢失。
2. **FTB 多方块建造任务。** 对整合包有用：可以要求玩家真正建成机器。接入 Lite 现有结构触发逻辑，使用 modern_tech 命名空间，并适配 Lite 机器等级及结构变体。
3. **FTB 配置与依赖。** 上游采用 SERVER 配置、默认开启；Lite 使用 STARTUP、默认关闭。移动配置时保留用户原设置或说明迁移。上游 FTB Quests 编译版本 2101.1.36，Lite 为 2100.1.3，增加任务 API 前应一并核验兼容范围。
4. **GuideME 布局。** 对 NEITHER 隐藏空图标占位，分别决定等级标签是否显示。不要恢复 EU 文字，也不要无条件删掉 Lite 的等级说明。
5. **NeoForge 基线。** 上游 21.1.238，Lite 21.1.228。可以单独升级并验证构建、可选兼容模组与游戏测试；版本差异本身不能证明 Lite 存在某个故障。

## 不应视为落后的差异

Lite 主动删除的管道、电缆网络、EU 电压机制、动态超频，以及卷曲板、叶片等中间材料，属于产品定位。上游仍有这些系统，不是 Lite 应补回的缺口。火药加速、超速模块按用户决定保留。

Lite 自己的 FE 能源、固定机器等级、配方 .level()、datagen 迁移和材料简化也不能用上游源文件直接覆盖。迁移上游修复时，应保留这些规则。

共同祖先前的 Create 兼容、EMI 配方树流体计算、GuideME 配方渲染等改动已在继承历史内，不应列为这次新增缺口。最近这 13 个提交没有引入新的大型机器或整套材料体系，也没有给出适用于 Lite FE 与固定等级模型的配方平衡方案。

上游的 EU 配方平衡不能直接证明 Lite 平衡合理。Lite 的能耗、等级门槛、加工时间和简化后的材料成本，仍需按自己的进度路线核算；相关已确认问题另见 PROJECT_CLEANUP_AUDIT.md。
