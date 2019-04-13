package fr.aquazus.diva.database.game;

import fr.aquazus.diva.database.generated.game.tables.records.ExperienceTableRecord;
import org.jooq.DSLContext;

import java.util.HashMap;

import static fr.aquazus.diva.database.generated.game.Tables.EXPERIENCE_TABLE;

public class ExperienceTable {

    private HashMap<Integer, Long> characterTable = new HashMap<>();
    private HashMap<Integer, Integer> mountTable = new HashMap<>();
    private HashMap<Integer, Integer> jobTable = new HashMap<>();
    private HashMap<Integer, Short> rankTable = new HashMap<>();

    ExperienceTable(DSLContext dsl) {
        for (ExperienceTableRecord record : dsl.selectFrom(EXPERIENCE_TABLE).fetch()) {
            if (record.getCharacter() != -1) characterTable.put(record.getLevel(), record.getCharacter());
            if (record.getMount() != -1) mountTable.put(record.getLevel(), record.getMount());
            if (record.getJob() != -1) jobTable.put(record.getLevel(), record.getJob());
            if (record.getRank() != -1) rankTable.put(record.getLevel(), record.getRank());
        }
    }

    public long getCharacterCap(int level) {
        return characterTable.getOrDefault(level, Long.MAX_VALUE);
    }

    public int getMountCap(int level) {
        return mountTable.getOrDefault(level, Integer.MAX_VALUE);
    }

    public int getJobCap(int level) {
        return jobTable.getOrDefault(level, Integer.MAX_VALUE);
    }

    public short getRankCap(int level) {
        return rankTable.getOrDefault(level, Short.MAX_VALUE);
    }
}
