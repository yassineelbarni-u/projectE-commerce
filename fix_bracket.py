import glob
import re
import os

for fpath in glob.glob("src/test/java/com/storeshop/ui/*.java"):
    with open(fpath, "r") as f:
        content = f.read()
    
    # We want to replace the random `    }\n\n    @BeforeEach` with `    @BeforeEach`
    content = re.sub(r'    }\n\n    @BeforeEach', '    @BeforeEach', content)
    
    with open(fpath, "w") as f:
        f.write(content)
