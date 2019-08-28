package datasets;

import annatations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    @Id
    private long id;
    private String name;
    private int age;
}
