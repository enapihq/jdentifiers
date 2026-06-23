package dk.ceti.jdentifiers.benchmarks;

import dk.ceti.jdentifiers.id.GID;
import dk.ceti.jdentifiers.id.ID;
import dk.ceti.jdentifiers.id.IDAble;
import dk.ceti.jdentifiers.id.LID;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Average-time benchmark for string conversion (parsing and formatting).
 *
 * <p>No pacing is applied — these are pure CPU operations with no time-dependent
 * state. {@link Mode#AverageTime} reports nanoseconds per operation, which reads
 * naturally for the sub-100ns paths measured here.
 *
 * <p>All inputs are non-final {@link State} fields assigned in {@link #setup()}
 * so the JIT cannot constant-fold string literals (the parse-reject paths in
 * particular collapse to a single cycle otherwise).
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
public class IDStringBenchmark implements IDAble {

    private LID<IDStringBenchmark> id32;
    private ID<IDStringBenchmark> id64;
    private GID<IDStringBenchmark> id128;
    private UUID testUuid;

    private int lidInt;
    private long idLong;

    private String lid32Lower;
    private String lid32Upper;
    private String id64Lower;
    private String id64Upper;
    private String gidCanonicalLower;
    private String gidCanonicalUpper;
    private String gidDashlessHex;
    private String gidInvalid;
    private String gidSignPrefix;
    private String uuidCanonicalLower;
    private String uuidCanonicalUpper;
    private String uuidSignPrefix;

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
            .include(".*" + IDStringBenchmark.class.getName() + ".*")
            .build())
            .run();
    }

    @Setup
    public void setup() {
        id32 = LID.fromString("6a677fc2");
        id64 = ID.fromString("6a677fc2ee05e1f6");
        id128 = GID.fromString("420bb7c1-4bb6-4936-9ab1-b6b81f9c0f61");
        testUuid = UUID.fromString("eee0d1bc-867e-4f08-99cc-35334bb3fee9");

        lidInt = 2_047_483_647;
        idLong = 9_223_372_036_854_775_807L;

        lid32Lower = "6a677fc3";
        lid32Upper = "6A677FC4";
        id64Lower = "6a677fc2ee05e1f7";
        id64Upper = "6A677FC2EE05E1F8";
        gidCanonicalLower = "de83dd89-d106-406c-8eff-53864a4b2d13";
        gidCanonicalUpper = "DE83DD89-D106-406C-8EFF-53864A4B2D13";
        gidDashlessHex = "de83dd89d106406c8eff53864a4b2d13";
        gidInvalid = "not-a-uuid";
        gidSignPrefix = "+e83dd89-d106-406c-8eff-53864a4b2d13";
        uuidCanonicalLower = "6f696d11-c46b-4800-b26d-6cdc452ecee6";
        uuidCanonicalUpper = "9FA73CAA-2F6A-4EF7-B868-154CE3BE68EF";
        uuidSignPrefix = "+e83dd89-d106-406c-8eff-53864a4b2d13";
    }

    @Benchmark
    public LID<IDAble> lid_32_bit_from_integer() {
        return LID.fromInt(lidInt);
    }

    @Benchmark
    public LID<IDAble> lid_32_bit_from_base16_string() {
        return LID.fromString(lid32Lower);
    }

    @Benchmark
    public LID<IDAble> lid_32_bit_from_base16_upper_case() {
        return LID.fromString(lid32Upper);
    }

    @Benchmark
    public String lid_32_bit_to_base16_string() {
        return id32.toString();
    }

    @Benchmark
    public ID<IDAble> id_64_bit_from_long() {
        return ID.fromLong(idLong);
    }

    @Benchmark
    public ID<IDAble> id_64_bit_from_base16_string() {
        return ID.fromString(id64Lower);
    }

    @Benchmark
    public ID<IDAble> id_64_bit_from_base16_upper_case() {
        return ID.fromString(id64Upper);
    }

    @Benchmark
    public String id_64_bit_to_base16_string() {
        return id64.toString();
    }

    @Benchmark
    public String id_64_bit_to_base64_string() {
        return id64.toBase64String();
    }

    @Benchmark
    public GID<IDAble> gid_128_bit_from_uuid() {
        return GID.fromUuid(testUuid);
    }

    @Benchmark
    public GID<IDAble> gid_128_bit_from_base16_string() {
        return GID.fromString(gidCanonicalLower);
    }

    @Benchmark
    public GID<IDAble> gid_128_bit_from_base16_upper_case() {
        return GID.fromString(gidCanonicalUpper);
    }

    @Benchmark
    public Optional<GID<IDAble>> gid_128_bit_parse_base16_string() {
        return GID.parseStrict(gidCanonicalLower);
    }

    @Benchmark
    public Optional<GID<IDAble>> gid_128_bit_parse_invalid() {
        return GID.parseStrict(gidInvalid);
    }

    @Benchmark
    public Optional<GID<IDAble>> gid_128_bit_parse_invalid_sign_prefix() {
        return GID.parseStrict(gidSignPrefix);
    }

    @Benchmark
    public Optional<GID<IDAble>> gid_128_bit_parse_lenient_canonical() {
        return GID.parseLenient(gidCanonicalLower);
    }

    @Benchmark
    public Optional<GID<IDAble>> gid_128_bit_parse_lenient_dashless_hex() {
        return GID.parseLenient(gidDashlessHex);
    }

    @Benchmark
    public String gid_128_bit_to_base16_string() {
        return id128.toString();
    }

    @Benchmark
    public UUID jdk_uuid_from_upper_case_string() {
        return UUID.fromString(uuidCanonicalUpper);
    }

    @Benchmark
    public UUID jdk_uuid_from_string() {
        return UUID.fromString(uuidCanonicalLower);
    }

    @Benchmark
    public String jdk_uuid_to_string() {
        return testUuid.toString();
    }

    @Benchmark
    public UUID jdk_uuid_fromString_invalid_sign_prefix() {
        return UUID.fromString(uuidSignPrefix);
    }
}
