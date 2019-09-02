package datasets;

import annatations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
    @Id
    private long no;
    private String type;
    private long rest;
}
