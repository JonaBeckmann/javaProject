package mybot;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.*;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class myBot extends AIWithComputationBudget {
    UnitTypeTable m_utt;
    List<Unit> bases;
    List<Unit> barracks;
    List<Unit> workers;
    List<Unit> heavy;
    List<Unit> light;
    List<Unit> ranged;
    List<Unit> resource;
    List<Unit> enemy_workers;

    List<Unit> enemy_barracks;
    List<Unit> enemy_bases;

    List<Unit> enemy_heavy;
    List<Unit> enemy_light;
    List<Unit> enemy_ranged;

    List<Unit> enemyUnit;



    Random r;
    PlayerAction pa;
    UnitAction ua;
    GameState gameState;
    PhysicalGameState pgs;


    int resourcesUsed = 0;
    /**
     * Constructs the controller with the specified time and iterations budget
     *
     */

    public class Position{
      int x;
      int y;
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
    };

    public myBot(UnitTypeTable utt) {
        super(-1 , -1);
        m_utt = utt;
    }

    @Override
    public void reset() {

    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        pa = new PlayerAction();
        r = new Random();

        gameState = gs;
        pgs = gs.getPhysicalGameState();

        bases = new ArrayList<>();
        barracks = new ArrayList<>();
        workers = new ArrayList<>();
        enemy_workers = new ArrayList<>();
        enemy_bases = new ArrayList<>();
        enemy_barracks = new ArrayList<>();
        heavy = new ArrayList<>();
        light = new ArrayList<>();;
        ranged = new ArrayList<>();;
        resource = new ArrayList<>();;
        enemy_heavy = new ArrayList<>();;
        enemy_light = new ArrayList<>();;
        enemy_ranged = new ArrayList<>();;
        enemyUnit = new ArrayList<>();
        resourcesUsed = 0;


        saveAllUnitsToList();

        workeraction();

        createBigUnits();

        heavyaction();

        rangedaction();

        lightaction();


        pa.fillWithNones(gameState,player,1);
        return pa;
    }
    private void lightaction() {
        if(light.size() > 0) {

            Position enemyBase = null; //Postion to save the enemybase
            if(enemy_bases.size() > 0) {
                enemyBase = new Position(enemy_bases.get(0).getX(), enemy_bases.get(0).getY());
            }
            Position enemyPos =  null; //save the position of enemy

            for (Unit l : light) {
                enemyPos = getnearstEnemyPosition(l); //get the possible nearst distance between ranged unit and worker unit

                if (!busy(l) && enemyPos != null) {

                    if(moveUnitToPos(l, enemyPos)) { //move all heavys to worker unit to attack them by calculating the shortes distance
                    }
                    else{// when there is no movement => this field is blocked by enemy

                        attackEnemy(l,enemyPos); //attack this worker
                    }
                }
                if(enemy_workers.size() == 0){ // when there is no enemyworkers than
                    if(!busy(l)) {
                        if(moveUnitToPos(l, enemyBase)){ //move all big units to the enemybase

                        }
                        else{ // and attack it.
                            attackEnemy(l,enemyBase);
                        }
                    }
                }
            }
        }
    }

    private void rangedaction() {
        if(ranged.size() > 0) {

            Position enemyBase = null; //Postion to save the enemybase
            if(enemy_bases.size() > 0) {
                enemyBase = new Position(enemy_bases.get(0).getX(), enemy_bases.get(0).getY());
            }
            Position enemyPos =  null; //save the position of enemy

            for (Unit r : ranged) {
                enemyPos = getnearstEnemyPosition(r); //get the possible nearst distance between ranged unit and worker unit

                if (!busy(r) && !barracks.isEmpty() && enemyPos != null) {

                    if(moveUnitToPos(r, enemyPos)) { //move all heavys to worker unit to attack them by calculating the shortes distance
                    }
                    else{// when there is no movement => this field is blocked by enemy

                        attackEnemy(r,enemyPos); //attack this worker
                    }
                }
                if(enemy_workers.size() == 0){ // when there is no enemyworkers than
                    if(!busy(r)) {
                        if(moveUnitToPos(r, enemyBase)){ //move all big units to the enemybase

                        }
                        else{ // and attack it.
                            attackEnemy(r,enemyBase);
                        }
                    }
                }
            }
        }
    }

    private void heavyaction() {

        //action when there are at least one heavy unit
        if(heavy.size() > 0) {

            Position enemyBase = null; //Postion to save the enemybase
            if(enemy_bases.size() > 0) {
                 enemyBase = new Position(enemy_bases.get(0).getX(), enemy_bases.get(0).getY());
            }
                 Position enemyPos =  null; //save the position of enemy

            for (Unit h : heavy) {
                  enemyPos = getnearstEnemyPosition(h); //get the possible nearst distance between heavy unit and worker unit

                if (!busy(h) && !barracks.isEmpty() && enemyPos != null) {

                   if(moveUnitToPos(h, enemyPos)) { //move all heavys to worker unit to attack them by calculating the shortes distance
                    }
                    else{// when there is no movement => this field is blocked by enemy

                        attackEnemy(h,enemyPos); //attack this worker
                   }
                }
                if(enemy_workers.size() == 0){ // when there is no enemyworkers than
                    if(!busy(h)) {
                        if(moveUnitToPos(h, enemyBase)){ //move all big units to the enemybase

                        }
                        else{ // and attack it.
                            attackEnemy(h,enemyBase);
                        }
                    }
                }
            }
        }
    }



    private Position getnearstEnemyPosition(Unit u) {


        Position shortestDistance = null;//position to save the shortestdistance
        int shortestDistanceValue = Integer.MAX_VALUE; // to calculate the shortestdistance

        for(int i = 0; i < enemyUnit.size();i++){// loop through the enemyworker list
            int distance = Math.abs(u.getX() - enemyUnit.get(i).getX()) + Math.abs(u.getY() - enemyUnit.get(i).getY());
            // and calculate the distance

             if (distance < shortestDistanceValue) { // if the current distance is less than the shortestdistance
                shortestDistance = new Position(enemyUnit.get(i).getX(),enemyUnit.get(i).getY()); // save this position
                shortestDistanceValue = distance; // and change the distance to the shortestdistance
            }

        }
         return shortestDistance;
    }

    private boolean checkBaseDestroyed() {
        if(bases.isEmpty()){
            return true;
        }
        else
            return false;
    }

    boolean busy(Unit u) {
        if(pa.getAction(u) != null)
            return true;
        UnitActionAssignment aa = gameState.getActionAssignment(u);
        return aa != null;
    }


    private void workeraction(){

        //produce new units, for example workers in the base
        if(!checkBaseDestroyed() && !busy(bases.get(0))) {
            if (workers.size() < 3) {
                boolean succ = produceNewUnits(bases.get(0), m_utt.getUnitType("Worker"), gameState.getTime() % 3);
            }
        }

        Position enemyBase = null;
        Position enemyWorker = null;
        if(!enemy_bases.isEmpty()) {
              enemyBase = new Position(enemy_bases.get(0).getX(), enemy_bases.get(0).getY());
        }
        if(!enemy_workers.isEmpty()){
            enemyWorker = new Position(enemy_workers.get(0).getX(), enemy_workers.get(0).getY());
        }

        List<Position> resources = getResourcePositions(); //List with positions of the resources
        Position base = null;
        if (!bases.isEmpty()){
            base = new Position(bases.get(0).getX(), bases.get(0).getY()); //set position of base
        }


        for(Unit a : workers) {

            //only worker 0 and 1 do harvest and return resources to base
            if (a == workers.get(0) ||  a == workers.get(1) ) {
                if (!busy(a) && a.getResources() == 1 && !bases.isEmpty()) {
                    moveUnitToPos(a, base); //move unit to base
                }
                if (!busy(a) && a.getResources() == 1 && !bases.isEmpty()) {
                    returnResource(a, base); //put resource in base
                }
                if (!busy(a) && !resources.isEmpty()) {
                        moveUnitToPos(a, resources.get(resources.size()-1)); //move unit to the position of the resource
                }
                if (!busy(a) && !resources.isEmpty()) {
                        harvest(a, resources.get(resources.size()-1)); //tell unit to harvest the resource
                }

            }
        }
        for(Unit a : workers){
            if(!busy(a) && barracks.isEmpty() && workers.size() > 2){
                createBarracks(a);
            }

        }
    }

    private boolean createBigUnits(){
        int random;
        random = r.nextInt(0,3); //just the random number
        // to create the heavy units up, down, left or right of the barracks
        Position pos;
        UnitAction ua = null;
        UnitType ut;

        new UnitAction(UnitAction.TYPE_PRODUCE, random, m_utt.getUnitType("Heavy"));



        if(gameState.getPlayer(0).getResources() - resourcesUsed < m_utt.getUnitType("Heavy").cost){
            return false;
        }
        if(barracks.size() < 1 || bases.size() < 1) {
            return false;

        }

            pos = futurePosition(barracks.get(0),random);
        if(pos == null){
            return false;
        }

            if (!checkPositionIsFree(pos.getX(), pos.getY())) {
                return false;
            }

            if(random == 0){
                ua = new UnitAction(UnitAction.TYPE_PRODUCE, random, m_utt.getUnitType("Ranged"));
            }


            if(random == 2) {
                ua = new UnitAction(UnitAction.TYPE_PRODUCE, random, m_utt.getUnitType("Heavy"));

            }


            if(ua == null){return false;}
             ut = ua.getUnitType();


                if (!gameState.isUnitActionAllowed(barracks.get(0), ua)){
                    return false;
                }

                if (!busy(barracks.get(0))) {
                    pa.addUnitAction(barracks.get(0), ua);
                    resourcesUsed += ut.cost;
                    return true;
                }


        return false;
    }
    private void createBarracks(Unit a){

        //create the barracks to the position of base by adding two to x and y
        int dir = 2;
        //when the worker is at this position then create the barracks
        if (checkPositionIsFree(bases.get(0).getX()+1, bases.get(0).getY()+1)) {
            int random;
            random = r.nextInt(0,2);
            if (random == 0){
                dir = 0;
            }
            else{
                dir = 1;
            }
        }
        if (a.getX() == bases.get(0).getX() + 1 && a.getY() == bases.get(0).getY()) {
            boolean succ = produceNewUnits(a, m_utt.getUnitType("Barracks"), dir);

        } else if (barracks.isEmpty()) { // else move to this position
            Position pos = new Position(bases.get(0).getX() + 1, bases.get(0).getY());
            moveUnitToPos(a, pos);
        }
    }

    //returns a list with all positions of the resources
    List<Position> getResourcePositions(){
        List<Position> resourcePositions = new ArrayList<>();
        for (Unit u : resource){
            Position pos = new Position(u.getX(), u.getY());
            resourcePositions.add(pos);
        }
        return resourcePositions;
    }

    //method for harvesting resources
    private boolean harvest(Unit a,Position p){
        Position workerPos = new Position(a.getX(), a.getY()); //get the position of the worker
        int dir = toDir(workerPos, p); //get the direction in which the unit has to face to harvest
        UnitAction ua = new UnitAction(UnitAction.TYPE_HARVEST, dir);

        if (!gameState.isUnitActionAllowed(a, ua)){
            return false;
        }
        pa.addUnitAction(a, ua);
        return true;
    }

    //return the collected resource to the base
    private boolean returnResource(Unit a, Position base){
        Position workerPos = new Position(a.getX(), a.getY()); //get position of the worker
        int dir = toDir(workerPos, base); //get the direction of the base
        UnitAction ua = new UnitAction(UnitAction.TYPE_RETURN, dir);

        if (!gameState.isUnitActionAllowed(a, ua)){
            return false;
        }
        pa.addUnitAction(a, ua);
        return true;
    }

    //check in which direction a destination is
    int toDir(Position src, Position dst) {
        int dx = dst.getX() - src.getX(); //get difference of the x values
        int dy = dst.getY() - src.getY(); //get difference of the y values
        int dirX;
        int dirY;
        //check which direction is needed
        if (dx > 0) {
            dirX = UnitAction.DIRECTION_RIGHT;
        } else {
            dirX = UnitAction.DIRECTION_LEFT;
        }
        if (dy > 0) {
            dirY = UnitAction.DIRECTION_DOWN;
        } else {
            dirY = UnitAction.DIRECTION_UP;
        }
        //if unit is in the adjacent square dx or dy is 1/0
        //return the direction with the value 1
        if (Math.abs(dx) > Math.abs(dy)){
            return dirX;
        }
        return dirY;
    }

    private boolean moveUnitToPos(Unit a, Position p){

        int random = r.nextInt(0,4);

        UnitAction ua;

        switch (random) {
            case 0 -> {
                if (p.getX() > a.getX()) {
                    ua = new UnitAction(1, UnitAction.DIRECTION_RIGHT);
                    if (gameState.isUnitActionAllowed(a, ua)) {
                        pa.addUnitAction(a, ua);
                    }
                    return true;
                }
            }
            case 1 -> {
                if (p.getX() < a.getX()) {
                    ua = new UnitAction(1, UnitAction.DIRECTION_LEFT);
                    if (gameState.isUnitActionAllowed(a, ua)) {
                        pa.addUnitAction(a, ua);
                    }
                    return true;
                }
            }
            case 2 -> {
                if (p.getY() < a.getY()) {
                    ua = new UnitAction(1, UnitAction.DIRECTION_UP);
                    if (gameState.isUnitActionAllowed(a, ua)) {
                        pa.addUnitAction(a, ua);
                    }
                    return true;
                }
            }
            case 3 -> {
                if (p.getY() > a.getY()) {
                    ua = new UnitAction(1, UnitAction.DIRECTION_DOWN);
                    if (gameState.isUnitActionAllowed(a, ua)) {
                        pa.addUnitAction(a, ua);
                    }
                    return true;
                }
            }

        }

        return false;

    }

    private boolean attackEnemy(Unit a,Position p){
        if(checkIfUnitIsEnemy(p)){
            ua = new UnitAction(5,p.getX(),p.getY());//make an attack if found unit on position x,y

            if(gameState.isUnitActionAllowed(a,ua)){
                pa.addUnitAction(a,ua);
                return true;
            }
        }
        return false;

    }
    private boolean checkIfUnitIsEnemy(Position p){
        Unit enemy = pgs.getUnitAt(p.getX(),p.getY());

        if(enemy == null){
            return false;
        }
        if(enemy.getPlayer() > 0){
            return true;
        }
        else
            return false;
    }

    boolean checkPositionIsFree(int x, int y){


        if(x < 0 || y < 0 || y >= pgs.getHeight() || x >= pgs.getWidth()){
            return false;
        }
        if(pgs.getUnitAt(x,y) != null){
           return false;
        }
        if(pgs.getHeight() < y && pgs.getWidth() < x){
            return false;
        }
        if(pgs.getTerrain(x,y) == 1){
            return false;
        }
        return true;
    }
    boolean produceNewUnits(Unit u, UnitType uType, int whereToSet){

        Position pos = futurePosition(u,whereToSet);

        //if the resources of player 0 is less than the cost of the new unit return false
        if(gameState.getPlayer(0).getResources() - resourcesUsed < uType.cost){
            return false;
        }
        else{
            if(!checkPositionIsFree(pos.getX(),pos.getY())){
                return false;
            }
            UnitAction ua = new UnitAction(UnitAction.TYPE_PRODUCE, whereToSet,uType);
            if (!gameState.isUnitActionAllowed(u, ua))
                return false;

                pa.addUnitAction(u, ua);
                resourcesUsed += uType.cost;
                return true;
        }


    }

    private void saveAllUnitsToList() {
        for (Unit u : pgs.getUnits()){
            if (u.getType() == m_utt.getUnitType("Worker") && u.getPlayer() == 0) {
                workers.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Worker") && u.getPlayer() == 1){
                enemy_workers.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Barracks") && u.getPlayer() == 0){
                barracks.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Barracks") && u.getPlayer() == 1){
                enemy_barracks.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Base") && u.getPlayer() == 0){
                bases.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Base") && u.getPlayer() == 1){
                enemy_bases.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Heavy") && u.getPlayer() == 0){
                heavy.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Heavy") && u.getPlayer() == 1){
                enemy_heavy.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Light") && u.getPlayer() == 0){
                light.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Light") && u.getPlayer() == 1){
                enemy_light.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Ranged") && u.getPlayer() == 0){
                ranged.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Ranged") && u.getPlayer() == 1){
                enemy_ranged.add(u);
                enemyUnit.add(u);
            }
            if(u.getType() == m_utt.getUnitType("Resource") && u.getX() == 0){
                resource.add(u);
            }
        }
    }

    Position futurePosition(Unit a,int direction){
        Position newPos;
        switch(direction){
            case 0:
                newPos =  new Position(a.getX(),a.getY()-1);
                break;
            case 1:
                newPos =  new Position(a.getX()+1,a.getY());
                break;
            case 2:
                newPos =  new Position(a.getX(),a.getY()+1);
                break;
            case 3:
                newPos =  new Position(a.getX()-1,a.getY());
                break;
            default:
                return newPos = null;
        }
        return newPos;
    }

    public boolean move(Unit a, int direction){

        Position pos = futurePosition(a,direction);

        if(pos == null){
            return false;
        }

        ua = new UnitAction(1,direction);



        if(checkPositionIsFree(pos.getX(),pos.getY())){

            if(gameState.isUnitActionAllowed(a,ua)) {
                pa.addUnitAction(a, ua);
            }
            return true;
        }
        else{
            if(checkIfUnitIsEnemy(pos)){

                ua = new UnitAction(5,pos.getX(),pos.getY());//make an attack if found unit on position x,y

                if(gameState.isUnitActionAllowed(a,ua)){
                    pa.addUnitAction(a,ua);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public AI clone() {
        return new myBot(m_utt);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        return new ArrayList<>();
    }
}
