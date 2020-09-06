package JJack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class GameScreen extends BaseScreen {

    private Player player;
    private World world;
    private int coins = 0;
    private ArrayList<Box2DActor> removeList;
    //game world dimensions
    final int mapWidth = 800;
    final int mapHeight = 600;

    public GameScreen(BaseGame g) {
        super(g);
    }

    public void addSolid(Texture texture, float x, float y, float w, float h){
        Box2DActor solid = new Box2DActor();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        solid.storeAnimation("default", texture);
        solid.setPosition(x, y);
        solid.setSize(w, h);
        mainStage.addActor(solid);
        solid.setStatic();
        solid.setShapeRectangle();
        solid.initializePhysics(world);
    }



    @Override
    public void create() {
        world = new World(new Vector2(0, -9.8f), true);
        removeList = new ArrayList<Box2DActor>();

        // background image
        BaseActor background = new BaseActor();
        Texture texture = new Texture(Gdx.files.internal("sky.png"));
        background.setTexture(texture);
        mainStage.addActor(background);

        // solid objects
        Texture groundTexture = new Texture(Gdx.files.internal("ground.png"));
        Texture dirtTexture = new Texture(Gdx.files.internal("dirt.png"));

        addSolid(groundTexture, 0, 0, 800, 32);
        addSolid(groundTexture, 150, 250, 100, 32);
        addSolid(groundTexture, 282, 250, 100, 32);

        addSolid(dirtTexture, 0,0, 32, 600);
        addSolid(dirtTexture, 768, 0, 32, 600);

        //adding dynamic objects of the game
        Box2DActor crate = new Box2DActor();
        Texture crateTexture = new Texture(Gdx.files.internal("crate.png"));
        crateTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        crate.storeAnimation("default", crateTexture);
        crate.setPosition(500, 100);
        mainStage.addActor(crate);
        crate.setDynamic();
        crate.setShapeRectangle();
        // set standard density, average friction, small restitution
        crate.setPhysicsProperties(1, 0.5f, 0.1f);
        crate.initializePhysics(world);

        Box2DActor ball = new Box2DActor();
        Texture ballTexture = new Texture(Gdx.files.internal("ball.png"));
        ballTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ball.storeAnimation("default", ballTexture);
        ball.setPosition(300, 320);
        mainStage.addActor(ball);
        ball.setDynamic();
        ball.setShapeCircle();
        // set standard density, small friction, average restitution
        ball.setPhysicsProperties(1, 0.1f, 0.5f);
        ball.initializePhysics(world);

        Coin baseCoin = new Coin();
        Texture coinTexture = new Texture(Gdx.files.internal("coin.png"));
        coinTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        baseCoin.storeAnimation("default", coinTexture);

        Coin coin1 = baseCoin.clone();
        coin1.setPosition(500,250);
        mainStage.addActor(coin1);
        coin1.initializePhysics(world);

        Coin coin2 = baseCoin.clone();
        coin2.setPosition(550, 250);
        mainStage.addActor(coin2);
        coin2.initializePhysics(world);

        Coin coin3 = baseCoin.clone();
        coin3.setPosition(600, 250);
        mainStage.addActor(coin3);
        coin3.initializePhysics(world);

        player = new Player();

        Animation walkAnimation = GameUtils.parseImageFiles("walk-",
                ".png",
                3,
                0.15f,
                Animation.PlayMode.LOOP_PINGPONG);
        player.storeAnimation("walk", walkAnimation);

        Texture standTexture = new Texture(Gdx.files.internal("stand.png"));
        standTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        player.storeAnimation("stand", standTexture);

        Texture jumpTexture = new Texture(Gdx.files.internal("jump.png"));
        jumpTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        player.storeAnimation("jump", jumpTexture);

        player.setPosition(164, 300);
        player.setSize(60,90);
        mainStage.addActor(player);
        player.setDynamic();
        player.setShapeRectangle();
        // set standard density, average friction, small restitution
        player.setPhysicsProperties(1, 0.5f, 0.1f);
        player.setFixedRotation();
        player.setMaxSpeedX(2);
        player.initializePhysics(world);

        world.setContactListener(
                new ContactListener() {
                    @Override
                    public void beginContact(Contact contact) {
                        Object objC = GameUtils.getContactObject(contact, Coin.class);
                        if (objC != null){
                            Object p = GameUtils.getContactObject(contact, Player.class, "main");
                            if (p != null){
                                Coin c = (Coin) objC;
                                removeList.add(c);
                            }

                            return; //avoid possible jumps
                        }
                        Object objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                        if (objP != null ){
                            Player p = (Player) objP;
                            p.adjustGroundCount(1);
                            p.setActiveAnimation("stand");
                        }
                    }

                    @Override
                    public void endContact(Contact contact) {
                        Object objC = GameUtils.getContactObject(contact, Coin.class);
                        if (objC != null)
                            return;
                        Object objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                        if (objP != null){
                            Player p = (Player) objP;
                            p.adjustGroundCount(-1);
                        }
                    }

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold) {

                    }

                    @Override
                    public void postSolve(Contact contact, ContactImpulse impulse) {

                    }
                }
        );
    }

    @Override
    public void update(float dt) {

        removeList.clear();
        world.step(1/60f, 6,2);
        for (Box2DActor ba: removeList){
            ba.destroy();
            world.destroyBody(ba.getBody());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.setScale(-1, 1);
            player.applyForce(new Vector2(-3.0f, 0));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.setScale(1,1);
            player.applyForce(new Vector2(3.0f, 0));
        }

        if (player.getSpeed() > 0.1 && player.getAnimationName().equals("stand"))
            player.setActiveAnimation("walk");
        if (player.getSpeed() < 0.1 && player.getAnimationName().equals("walk"))
            player.setActiveAnimation("stand");

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.P)
            togglePaused();

        if (keycode == Input.Keys.R)
            game.setScreen(new GameScreen(game));

        if (keycode == Input.Keys.SPACE && player.isOnGround()) {
            Vector2 jumpVector = new Vector2(0,3);
            player.applyImpulse(jumpVector);
            player.setActiveAnimation("jump");
        }

        return false;
    }
}
