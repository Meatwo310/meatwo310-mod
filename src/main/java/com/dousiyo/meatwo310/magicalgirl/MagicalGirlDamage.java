package com.dousiyo.meatwo310.magicalgirl;

public final class MagicalGirlDamage {
    private static final float GLOBAL_ATTACK_DAMAGE_MULTIPLIER = 1.5F;

    private MagicalGirlDamage() {
    }

    private static float scale(float damage) {
        return damage * GLOBAL_ATTACK_DAMAGE_MULTIPLIER;
    }

    private static double scale(double damage) {
        return damage * GLOBAL_ATTACK_DAMAGE_MULTIPLIER;
    }

    // 魔法少女本体の近接攻撃力。
    public static final double BOSS_MELEE_ATTRIBUTE = scale(8.0D);
    // 分身ファントムの通常攻撃力。
    public static final double PHANTOM_ATTACK_ATTRIBUTE = scale(6.0D);

    // 通常の魔法陣ハザードの継続ダメージ。
    public static final float BASIC_HAZARD = scale(5.0F);
    // 毒ガス系ハザードの継続ダメージ。
    public static final float GAS_HAZARD = scale(3.0F);
    // 最終段階ハザードの継続ダメージ。
    public static final float FINAL_HAZARD = scale(6.0F);
    // 黄色い発火煙ハザードの継続ダメージ。
    public static final float YELLOW_IGNITION_HAZARD = scale(8.0F);
    // 放射線ガスハザードの継続ダメージ。
    public static final float RADIATION_HAZARD = scale(4.0F);

    // 右手召喚の叩きつけ/接触攻撃ダメージ。
    public static final float MAGIC_RIGHT_HAND = scale(10.0F);
    // 色付き雷Entity自身が持つ基本ダメージ。
    public static final float COLORED_LIGHTNING_ENTITY = scale(8.0F);
    // 色付き雷嵐で着弾地点周辺に直接与えるダメージ。
    public static final float COLORED_LIGHTNING_STRIKE = scale(8.0F);
    // 地面を走る雷攻撃のダメージ。
    public static final float GROUND_LIGHTNING = scale(7.0F);

    // ステラバーストの通常弾ダメージ。
    public static final float STELLA_BURST = scale(4.5F);
    // フェーズ2で飛ばす星弾のダメージ。
    public static final float PHASE_2_STELLA_BURST = 4.5F * 3.0F;
    // ステラバーストEntityを直接生成した時の初期ダメージ。
    public static final float STELLA_BURST_DEFAULT = scale(5.0F);
    // グランドノヴァ中に周回して撃たれるステラバーストのダメージ。
    public static final float GRAND_NOVA_STELLA_BURST = scale(4.0F);
    // グランドノヴァ終盤の範囲ヒット1回分のダメージ。
    public static final float GRAND_NOVA_TICK = scale(7.0F);
    // ダイヤスピナーが命中した時のダメージ。
    public static final float DIAMOND_SPINNER = scale(9.0F);
    // リボンジャッジメント通常レーザーの1ヒットダメージ。
    public static final float RIBBON_JUDGEMENT = scale(6.0F);
    // リボンジャッジメント赤レーザーの1ヒットダメージ。
    public static final float RIBBON_JUDGEMENT_RED = scale(3.5F);
    // ルーンケージの拘束ビーム1ヒットダメージ。
    public static final float RUNE_CAGE_BEAM = scale(6.0F);

    // 化学エリアEntityの初期ダメージ。
    public static final float CHEMICAL_AREA_DEFAULT = scale(2.0F);
    // 化学スプレーで置く化学エリアの継続ダメージ。
    public static final float CHEMICAL_SPRAY_AREA = scale(2.0F);
    // リアクション弾の下準備で置く化学エリアの継続ダメージ。
    public static final float REACTION_BULLET_AREA = scale(2.0F);
    // 緊急処刑コンボで置く化学エリアの継続ダメージ。
    public static final float EMERGENCY_CHEMICAL_AREA = scale(2.2F);
    // リアクション弾で化学エリアを爆発させた時のダメージ。
    public static final float CHEMICAL_REACTION = scale(8.0F);
    // 緊急処刑コンボ中の化学反応爆発ダメージ。
    public static final float EMERGENCY_CHEMICAL_REACTION = scale(13.0F);
    // 緊急処刑コンボの最後の大爆発ダメージ。
    public static final float EMERGENCY_FINAL_EXPLOSION = scale(14.0F);
    // 化学エリアが燃えた時の距離減衰爆発ダメージの下限。
    public static final float CHEMICAL_AREA_EXPLOSION_MIN = scale(5.0F);
    // 化学エリアが燃えた時の距離減衰爆発ダメージの基準値。
    public static final float CHEMICAL_AREA_EXPLOSION_BASE = scale(11.0F);

    // ロックオンバーストのTaCZ弾ダメージ。
    public static final float SUPERB_LOCK_ON_SHOT = scale(4.5F);
    // 制圧射撃の雨で降るTaCZ弾ダメージ。
    public static final float SUPERB_SUPPRESSION_RAIN = scale(4.0F);
    // 緊急処刑コンボ中に降るTaCZ弾ダメージ。
    public static final float SUPERB_EMERGENCY_RAIN = scale(4.2F);
    // 浮遊TaCZ銃が撃つ弾のダメージ。
    public static final float FLOATING_TACZ_GUN_SHOT = scale(5.8F);

    // 注射針Entityを直接生成した時の初期ダメージ。
    public static final float INJECTION_NEEDLE_DEFAULT = scale(3.0F);
    // 注射針攻撃の命中ダメージ。
    public static final float INJECTION_NEEDLE = scale(3.0F);

    // 根の槍が地面から出て当たった時のダメージ。
    public static final float ROOT_LANCE = scale(5.0F);
    // 花の範囲攻撃のダメージ。
    public static final float BLOOM_CIRCLE = scale(6.0F);
    // ツタ拘束のヒットダメージ。
    public static final float IVY_BIND = scale(2.0F);
    // 花びら突風のダメージ。
    public static final float PETAL_GALE = scale(4.0F);
    // 竜巻Entityを直接生成した時の1tick分初期ダメージ。
    public static final float TWISTER_TICK_DEFAULT = scale(1.1F);
    // 竜巻攻撃の継続1ヒットダメージ。
    public static final float TWISTER_TICK = scale(1.2F);
    // 竜巻終了時の吹き飛ばしダメージ倍率。
    public static final float TWISTER_FINISH_MULTIPLIER = 7.0F;
    // 桜爪攻撃のダメージ。
    public static final float SAKURA_CLAW = scale(5.0F);
    // 桜雨の1ヒットダメージ。
    public static final float SAKURA_RAIN = scale(1.0F);
    // グランドブロッサムの範囲ダメージ。
    public static final float GRAND_BLOSSOM = scale(12.0F);
}
